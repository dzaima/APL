package APL;

import APL.errors.*;
import APL.tokenizer.Token;
import APL.tokenizer.types.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.*;
import APL.types.functions.*;
import APL.types.functions.builtins.AbstractSet;
import APL.types.functions.builtins.*;
import APL.types.functions.builtins.dops.*;
import APL.types.functions.builtins.fns.*;
import APL.types.functions.builtins.mops.*;
import APL.types.functions.trains.*;
import APL.types.functions.userDefined.UserDefined;

import java.util.*;


public class Exec {
  private final Scope sc;
  private final List<Token> tokens;
  private final LineTok allToken;
  
  public Exec(LineTok ln, Scope sc) {
    tokens = ln.tokens;
    allToken = ln;
    this.sc = sc;
  }
  
  
  
  private void printlvl(Object... args) {
    if (!Main.debug) return;
    for (int i = 0; i < Main.printlvl; i++) Main.print("  ");
    Main.printdbg(args);
  }
  private Stack<Token> left;
  public Obj exec() {
    if (tokens.size() > 0) Main.faulty = tokens.get(0);
    else Main.faulty = allToken;
    if (sc.alphaDefined && tokens.size() >= 2 && tokens.get(0) instanceof OpTok && ((OpTok) tokens.get(0)).op.equals("⍺") && tokens.get(1) instanceof SetTok) {
      if (Main.debug) printlvl("skipping cuz it's ⍺←");
      return null;
    }
    left = new Stack<>();
    left.addAll(tokens);
    if (Main.debug) {
      StringBuilder repr = new StringBuilder();
      for (Token t : tokens) repr.append(t.toRepr()).append(" ");
      printlvl("NEW:");
      Main.printlvl++;
      printlvl("code:", repr);
      printlvl();
    }
    reset();
    ArrayList<Obj> arr = null;
    while (left.size() > 0) {
      Token t = left.pop();
      Obj c;
      if (t instanceof NameTok && left.size() >= 2
        && left.peek() instanceof OpTok
        && ((OpTok) left.peek()).op.equals(".")
        && left.get(left.size() - 2) instanceof NameTok) {
        int ptr = left.size() - 2;
        while (ptr >= 2) {
          if (left.get(ptr - 1) instanceof OpTok
            && ((OpTok) left.get(ptr - 1)).op.equals(".")
            && left.get(ptr - 2) instanceof NameTok) ptr -= 2;
          else break;
        }
        String[] names = new String[(left.size() - ptr >> 1) + 1];
        names[names.length - 1] = ((NameTok) t).name;
        for (int i = names.length - 2; i >= 0; i--) {
          OpTok dot = (OpTok) left.pop();
          assert dot.op.equals(".");
          NameTok name = (NameTok) left.pop();
          names[i] = name.name;
        }
        
        if (Main.debug) printlvl("dotnot", Arrays.toString(names)); // todo fix (m).a (m).b0
        Obj d = null;
        Settable r = sc.getVar(names[0]);
        for (int i = 1; i < names.length; i++) {
          if (r == null) {
            r = sc.getVar(names[i]);
            if (Main.debug) printlvl(":start", d, r, names[i]);
          } else {
            var got = r.getOrThis();
            if (got instanceof Fun) {
              if (Main.debug) printlvl(":fn", d, r, names[i]);
              if (d == null) d = got;
              else d = new DotBuiltin().derive(d, got);
              r = sc.getVar(names[i]);
            } else if (got instanceof APLMap) {
              if (Main.debug) printlvl(":map", d, r, names[i]);
              r = ((APLMap) got).get(names[i]);
            } else throw new SyntaxError("dot-chain contained non-fn/map");
          }
        }
        if (r != null) {
          if (d == null) d = r;
          else d = new DotBuiltin().derive(d, r.get());
        } else if (d == null) throw new SyntaxError("what?");
        c = d;
        if (Main.debug) printlvl(llToString());
        
      } else {
        c = valueOf(t);
      }
      if (c.isObj() || c.type() == Type.gettable && (left.size() == 0 || !(left.get(0) instanceof SetTok))) {
        if (arr == null) arr = new ArrayList<>();
        arr.add(c);
      } else {
        if (arr != null) {
          if (arr.size() == 1) addS(arr.get(0));
          else addS(VarArr.of(arr));
          update(false);
          arr = null;
        }
        addS(c);
        update(false);
      }
    }
    if (arr != null) {
      if (arr.size() == 1) addS(arr.get(0));
      else addS(VarArr.of(arr));
    }
    update(true);
    
    
    Main.printlvl--;
    if (Main.debug) printlvl("END:", llToString());
    if (llSize != 1) {
      if (llSize == 0) return null;
      if (pollS().token != null) Main.faulty = pollS().token;
      // try to figure out what went wrong
      
      
      for (Node cn = LN.l; cn != FN; cn = cn.l) {
        Obj obj = cn.val;
        if (obj instanceof Variable) {
          Variable vobj = (Variable) obj;
          if (vobj.getOrThis() == obj) throw new SyntaxError("Couldn't find the value of " + vobj.name, obj);
        } else if (obj instanceof Settable) {
          Settable settable = (Settable) obj;
          if (settable.getOrThis() == obj) throw new SyntaxError("Couldn't find the value of " + obj, obj);
        }
      }
      
      // oh well that failed
      throw new SyntaxError("couldn't join everything up into a single expression", pollL());
    }
    return pollS();
  }
  private void update(boolean end) {
    if (llSize == 1 && pollS() == null) return;
    while (llSize != 1) {
      if (Main.debug) printlvl(llToString());
      if (is("D!|NFN", end, false)) {
        if (Main.debug) printlvl("NFN");
        var w = lastVal();
        var f = lastFun();
        var a = lastVal();
        Main.faulty = f;
        var res = f.callObj(a, w);
        if (res == null && (left.size() > 0 || llSize > 0)) throw new SyntaxError("trying to use result of function which returned nothing", a);
        if (res != null) addE(res);
        else return;
        continue;
      }
      if (llSize >= 2 && FN.r.r.type == '@') {
        if (is("F@", end, true)) {
          if (Main.debug) printlvl("F[]");
          var f = (Fun) firstObj();
          var w = (Brackets) popS();
          addS(new DervDimFn(f, w.toInts(), sc));
          continue;
        }
        if (is("M@", end, true)) {
          if (Main.debug) printlvl("M[]");
          var f = firstMop();
          var w = (Brackets) popS();
          addS(new DervDimMop(f, w.toInt(), sc));
          continue;
        }
        if (is("D@", end, true)) {
          if (Main.debug) printlvl("D[]");
          var f = firstDop();
          var w = (Brackets) popS();
          addS(new DervDimDop(f, w.toInt(), sc));
          continue;
        }
        if (is("v@", end, true)) {
          if (Main.debug) printlvl("v[]");
          var f = firstVar();
          var w = (Brackets) popS();
          addS(new Pick((Variable) f, w, sc));
          continue;
        }
        if (is("N@", end, true)) {
          if (Main.debug) printlvl("n[]");
          var a = firstVal();
          var w = (Brackets) popS();
          addS(RShoeUBBuiltin.on(w.val, a, sc.IO, w));
          continue;
        }
      }
      if (is("[FM←]|FN", end, false)) {
        if (Main.debug) printlvl("FN");
        var w = lastVal();
        var f = lastFun();
        Main.faulty = f;
        var res = f.callObj(w);
        if (res == null && (left.size() > 0 || llSize > 0)) throw new SyntaxError("trying to use result of function which returned nothing", f);
        if (res != null) addE(res);
        else return;
        continue;
      }
      if (is("#!←", end, true) || llSize == 1 && pollS().type() == Type.gettable) {
        var w = firstVar();
        addFirst(w.get());
      }
      
      if (llSize>2 && LN.l.l.type=='←') {
        if (is(new String[]{"D!|V←[#NFMD]","#←[#NFMDV]","D!|D←D","D!|M←M","D!|F←F","D!|N←N"}, end, false)) { // "D!|.←." to allow changing type
          if (Main.debug) printlvl("N←.");
          var w = lastObj();
          var s = (AbstractSet) popE(); // ←
          var a = popE(); // variable
          Main.faulty = s;
          var res = s.callObj(a, w, false);
          addE(res);
          continue;
        }
        if (is("D!|NF←N", end, false, 5)) {
          if (Main.debug) printlvl("NF←.");
          var w = lastVal();
          var s0 = popE(); // ←
          if (s0 instanceof DerivedSet) throw new SyntaxError("cannot derive an already derived ←");
          var s = (SetBuiltin) s0;
          var f = lastFun();
          Obj a = popE(); // variable
          Main.faulty = f;
          Obj res = s.callObj(f, a, w);
          if (res != null) addE(res);
          continue;
        }
      }
  
      if (llSize == 2 && is("F←", false, false)) {
        if (Main.debug) printlvl("F←");
        var s0 = popE(); // ←
        if (s0 instanceof DerivedSet) throw new SyntaxError("cannot derive an already derived ←");
        var s = (SetBuiltin) s0;
        var f = lastFun();
        addE(new DerivedSet(s, f));
        continue;
      }
      
      if (is("!D|[FN]M", end, true)) {
        if (Main.debug) printlvl("FM");
        var f = firstObj();
        var o = firstMop();
        addFirst(o.derive(f));
        continue;
      }
      if (is("!D|[FNV]D[FNV]", end, true)) {
        if (Main.debug) printlvl("FDF");
        var aa = popB(); // done.removeFirst();
        var  o = firstDop(); // (Dop) done.removeFirst();
        var ww = popB();
        var aau = aa;
        var wwu = ww;
        if (aau instanceof Settable) aau = ((Settable) aau).getOrThis();
        if (wwu instanceof Settable) wwu = ((Settable) wwu).getOrThis();
        if (aau instanceof VarArr) aau = ((VarArr) aau).get();
        if (wwu instanceof VarArr) wwu = ((VarArr) wwu).get();
        if (o instanceof DotBuiltin && aau instanceof APLMap && ww instanceof Variable) {
          addB(((APLMap) aau).get(Main.toAPL(((Variable) ww).name)));
        } else {
          addB(o.derive(aau, wwu));
        }
        continue;
      }
      if (is("D!|[FN]FF", end, false)) {
        if (Main.debug) printlvl("f g h");
        var h = lastFun();
        var g = lastFun();
        var f = lastObj();
        addE(new Fork(f, g, h));
        continue;
      }
      if (is("D!|NF", false, false)) {
        if (Main.debug) printlvl("A f");
        var f = lastFun();
        var a = lastObj();
        addE(new Atop(a, f));
        continue;
      }
      if (is("←FF", false, false)) {
        if (Main.debug) printlvl("g h");
        var h = lastFun();
        var g = lastObj();
        addE(new Atop(g, h));
        continue;
      }
      if (llSize >= 3 && pollS() instanceof JotBuiltin && FN.r.r.val instanceof DotBuiltin) {
        if (Main.debug) printlvl("∘.");
        var jot = popS();
        popS();
        var fn = popS();
        if (fn instanceof Settable) fn = ((Settable) fn).get();
        if (fn instanceof VarArr) fn = ((VarArr) fn).get();
        var TB = new TableBuiltin();
        TB.token = jot.token;
        addS(TB.derive(fn));
        continue;
      }
      break;
    }
    if (end && llSize == 2) {
      if (Main.debug) printlvl("g h");
      var h = lastFun();
      var g = lastObj();
      if (g instanceof Fun || g instanceof Value) addE(new Atop(g, h));
      else throw new SyntaxError("creating an atop with "+g.humanType(true), g);
    }
  }
  
  private Value lastVal() {
    var r = popE();
    if (r instanceof Settable) r = ((Settable) r).get();
    if (r instanceof Value) return (Value) r;
    if (r instanceof VarArr) return ((VarArr) r).get();
    throw new SyntaxError("Expected value, got "+r, r);
  }
  
  private Fun lastFun() {
    var r = popE();
    if (r instanceof Settable) r = ((Settable) r).get();
    if (r instanceof Fun) return (Fun) r;
    throw new SyntaxError("Expected function, got "+r, r);
  }
  
  private Value firstVal() {
    var r = popB();
    if (r instanceof Settable) r = ((Settable) r).get();
    if (r instanceof Value) return (Value) r;
    if (r instanceof VarArr) return ((VarArr) r).get();
    throw new SyntaxError("Expected value, got "+r, r);
  }
  private Dop firstDop() {
    var r = popB();
    if (r instanceof Settable) r = ((Settable) r).get();
    if (r instanceof Dop) return (Dop) r;
    throw new SyntaxError("Expected dop, got "+r, r);
  }
  
  private Obj lastObj() {
    var r = popE();
    if (r instanceof Settable) r = ((Settable) r).get();
    if (r instanceof VarArr) return ((VarArr) r).get();
    return r;
  }
  private Obj firstObj() {
    var r = popB();
    if (r instanceof VarArr) return ((VarArr) r).get();
    if (r instanceof Settable) return ((Settable) r).get();
    return r;
  }
  private Settable firstVar() {
    var r = popB();
    if (r instanceof Settable) return (Settable) r;
    throw new SyntaxError("Expected a variable, got "+r, r);
  }
  private Mop firstMop() {
    var r = popB();
    if (r instanceof Settable) r = ((Settable) r).get();
    if (r instanceof Mop) return (Mop) r;
    throw new SyntaxError("Expected mop, got "+r, r);
  }
  private void addFirst(Obj o) {
    addB(o);
  }
  
  
  
  
  private int llSize;
  private Obj popS() {
    llSize--;
    Node c = FN.r;
    Node r = c.r;
    Obj res = c.val;
    FN.r = c.r;
    r.l = FN;
    return res;
  }
  private Obj popE() {
    llSize--;
    Node c = LN.l;
    Node l = c.l;
    Obj r = c.val;
    LN.l = c.l;
    l.r = LN;
    return r;
  }
  private Obj popB() {
    llSize--;
    Obj r = barNode.remove();
    barNode = barNode.r;
    return r;
  }
  private void addS(Obj o) {
    llSize++;
    Node r = FN.r;
    Node l = FN.r.l;
    assert l == FN;
    Node n = new Node(o, l, r);
    l.r = n;
    r.l = n;
  }
  private void addE(Obj o) {
    llSize++;
    Node l = LN.l;
    Node r = LN.l.r;
    assert r == LN : llToString();
    Node n = new Node(o, l, r);
    l.r = n;
    r.l = n;
  }
  private void addB(Obj o) {
    llSize++;
    Node l = barNode.l;
    Node r = barNode;
    Node n = new Node(o, l, r);
    l.r = n;
    r.l = n;
    barNode = n;
  }
  private Obj pollS() {
    return FN.r.val;
  }
  private Obj pollL() {
    return LN.l.val;
  }
  private Node FN, LN;
  private void reset() {
    FN = new Node();
    LN = new Node();
    FN.r = LN;
    LN.l = FN;
    FN.l = LN.r = null;
  }
  private static class Node {
    Node l, r;
    char type;
    Obj val;
    Node() { }
    Node(Obj val, Node l, Node r) {
      this.l = l;
      this.r = r;
      this.val = val;
      type = val.type().chr;
    }
    Obj remove() {
      l.r = r;
      r.l = l;
      return val;
    }
    public String toString() {
      // return hashCode()+"{"+l.hashCode()+"; "+r.hashCode()+"}\n";
      return val==null? "null" : val.toString();
    }
  }
  private String llToString() {
    StringBuilder r = new StringBuilder("[");
    Node c = FN.r;
    boolean first = true;
    while (c != LN) {
      if (first) first = false;
      else r.append(", ");
      r.append(c);
      c = c.r;
    }
    return r.append("]").toString();
  }
  
  
  
  
  private boolean is(String[] pts, boolean everythingDone, boolean fromStart) {
    for (String pt : pts) if (is(pt, everythingDone, fromStart)) return true;
    return false;
  }
  private Node barNode;
  private boolean is(String pt, boolean everythingDone, boolean fromStart) {
    return is(pt, everythingDone, fromStart, 4);
  }
  private boolean is(String pt, boolean everythingDone, boolean fromStart, int am) {
    if(!fromStart && llSize > am) return false;
    if (everythingDone && is(pt, false, fromStart)) return true;
    if (fromStart && everythingDone) {
      for (int i = 0; i < pt.length(); i++) {
        if (pt.charAt(i) == '|') return is(pt.substring(i+1), false, true);
      }
    }
    int len = pt.length();
    int ptrinc = fromStart ? 1 : -1;
    boolean pass = false;
    barNode = FN.r;
    Node cn = fromStart? FN.r : LN.l;
    for (int i = fromStart ? 0 : len - 1; fromStart ? i<len : i>=0; i += ptrinc) {
      char p = pt.charAt(i);
      boolean inv = false;
      if (p == '|') {
        pass = everythingDone;
        barNode = cn;
        i += ptrinc;
        p = pt.charAt(i);
      }
      if (cn==FN | cn==LN) return pass;
      if (p == '!') {
        inv = true;
        i += ptrinc;
        p = pt.charAt(i);
      }
      Obj v = cn.val;
      if (p == 'v') {
        if (!(v instanceof Settable) ^ inv) return false;
        cn = fromStart? cn.r : cn.l;
        continue;
      }
      if (p == '.') {
        cn = fromStart? cn.r : cn.l;
        continue;
      }
      
      char type = cn.type;
      if (p == ']') { // regular guaranteed
        i--;
        boolean nf = true;
        while (true) {
          char c = pt.charAt(i);
          if (c == '[') break;
          if (c==type) nf = false;
          i--;
        }
        if (nf) return false; // no inv for []s!
      } else if (p == '[') { // reverse guaranteed
        i++;
        boolean nf = true;
        while (true) {
          char c = pt.charAt(i);
          if (c == ']') break;
          if (c==type) nf = false;
          i++;
        }
        if (nf) return false;
      } else {
        if (p!=type ^ inv) return false;
      }
      cn = fromStart? cn.r : cn.l;
    }
    return true;
  }
  
  private Obj valueOf(Token t) {
    Obj o = valueOfRaw(t);
    o.token = t;
    return o;
  }
  
  private Obj valueOfRaw(Token t) {
    if (t instanceof OpTok) {
      OpTok t1 = (OpTok) t;
      switch (t1.op.charAt(0)) {
        // slashes: / - reduce; ⌿ - replicate; \ - reduce (r[3]←(r[2] ← (r[1]←a) f b) f c); ⍀ - extend
        // in Dyalog but not at least partially implemented: ⊆⌹→  &⌶⌺
        // fns
        case '+': return new PlusBuiltin();
        case '-': return new MinusBuiltin();
        case '×': return new MulBuiltin();
        case '÷': return new DivBuiltin();
        case '*': return new StarBuiltin();
        case '⍟': return new LogBuiltin();
        case '√': return new RootBuiltin();
        case '⌈': return new CeilingBuiltin();
        case '⌊': return new FloorBuiltin();
        case '|': return new StileBuiltin();
        case '∧': return new AndBuiltin();
        case '∨': return new OrBuiltin();
        case '⍲': return new NandBuiltin(sc);
        case '⍱': return new NorBuiltin(sc);
        case '⊥': return new UTackBuiltin();
        case '⊤': return new DTackBuiltin();
        case '~': return new TildeBuiltin();
        case '○': return new TrigBuiltin();
        case '!': return new ExclBuiltin();
        
        case '∊': return new EpsilonBuiltin();
        case '⍷': return new FindBuiltin();
        case '⊂': return new LShoeBuiltin();
        case '⊇': return new RShoeUBBuiltin(sc);
        case '⊃': return new RShoeBuiltin(sc);
        case '∪': return new DShoeBuiltin();
        case '∩': return new UShoeBuiltin();
        case '⌷': return new SquadBuiltin(sc);
        case '⍳': return new IotaBuiltin(sc);
        case '⍸': return new IotaUBBuiltin(sc);
        case '⍴': return new RhoBuiltin();
        case ',': return new CatBuiltin();
        case '≢': return new TallyBuiltin();
        case '≡': return new DepthBuiltin();
        case '⊢': return new RTackBuiltin();
        case '⊣': return new LTackBuiltin();
        case '↑': return new UpArrowBuiltin();
        case '↓': return new DownArrowBuiltin();
        case '?': return new RandBuiltin(sc);
        case '⍪': return new CommaBarBuiltin();
        case '⍉': return new TransposeBuiltin();
        case '⊖': return new FlipBuiltin();
        case '⌽': return new ReverseBuiltin();
        
        case '…': return new EllipsisBuiltin();
        case '⍮': return new SemiUBBuiltin();
        case '⍕': return new FormatBuiltin();
        case '⍎': return new EvalBuiltin(sc);
        case '⍋': return new GradeUpBuiltin(sc);
        case '⍒': return new GradeDownBuiltin(sc);
        case '⌿': return new ReplicateBuiltin();
        case '⍀': return new ExpandBuiltin();
        case '⍧': return new LShoeStileBuiltin();
        case '%': return new MergeBuiltin(sc);
        
        // comparisons
        case '<': return new LTBuiltin();
        case '≤': return new LEBuiltin();
        case '=': return new EQBuiltin();
        case '≥': return new GEBuiltin();
        case '>': return new GTBuiltin();
        case '≠': return new NEBuiltin();
        
        // mops
        case '/': return new ReduceBuiltin();
        case '\\':return new ScanBuiltin();
        case '¨': return new EachBuiltin();
        case '⍨': return new SelfieBuiltin();
        case '⌾': return new TableBuiltin();
        case '⌸': return new KeyBuiltin(sc);
        case '⍁': return new ObliqueBuiltin();
        case '⍩':
        case 'ᐵ': return new EachLeft();
        case 'ᑈ': return new EachRight();
        
        // dops
        case '∘': return new JotBuiltin();
        case '⍛': return new JotUBBuiltin();
        case '.': return new DotBuiltin();
        case '⍣': return new RepeatBuiltin(sc);
        case '⍡': return new CRepeatBuiltin(sc);
        case '⍤': return new JotDiaeresisBuiltin();
        case '⍥': return new OverBuiltin();
        case '⍢': return new DualBuiltin();
        case '@': return new AtBuiltin(sc);
        case '⍫': return new ObverseBuiltin();
        
        
        case '⍬': return new DoubleArr(DoubleArr.EMPTY);
        case '⎕': return new Quad(sc);
        case '⍞': return new QuoteQuad();
        case '⍺': Obj o = sc.get("⍺"); if(o == null) throw new SyntaxError("No ⍺ found", t); return o;
        case '⍵':     o = sc.get("⍵"); if(o == null) throw new SyntaxError("No ⍵ found", t); return o;
        case '∇':     o = sc.get("∇"); if(o == null) throw new SyntaxError("No ∇ found", t); return o;
        case '⍶':     o = sc.get("⍶"); if(o == null) throw new SyntaxError("No ⍶ found", t); return o;
        case '⍹':     o = sc.get("⍹"); if(o == null) throw new SyntaxError("No ⍹ found", t); return o;
        default: throw new NYIError("no built-in " + ((OpTok) t).op + " defined in exec", t);
      }
    }
    if (t instanceof NumTok) return ((NumTok) t).num;
    if (t instanceof ChrTok) return ((ChrTok) t).val;
    if (t instanceof StrTok) return ((StrTok) t).val;
    if (t instanceof SetTok) return SetBuiltin.inst;
    if (t instanceof NameTok) return sc.getVar(((NameTok) t).name);
    if (t instanceof LineTok) return Main.exec((LineTok) t, sc);
    if (t instanceof ParenTok) {
      List<LineTok> ts = ((ParenTok) t).tokens;
      int size = ts.size();
      if (size == 0) return new StrMap();
      LineTok fst = ts.get(0);
      if (size==1 && fst.colonPos()==-1) {
        if (((ParenTok) t).hasDmd) return new Shape1Arr(Main.vexec(ts.get(0), sc));
        return Main.exec(ts.get(0), sc);
      }
      if (fst.tokens != null && fst.colonPos() != -1) { // map constants
        Scope nsc = new Scope(sc);
        StrMap res = new StrMap(nsc);
        for (LineTok ct : ts) {
          Token name = ct.tokens.get(0);
          if (ct.colonPos() ==-1) throw new SyntaxError("expected a colon in expression", ct.tokens.get(0));
          if (ct.colonPos() != 1) throw new SyntaxError("expected : to be the 2nd token in parenthesis", ct.tokens.get(ct.colonPos()));
          String key;
          if (name instanceof NameTok) key = ((NameTok) name).name;
          else if (name instanceof StrTok) key = ((StrTok) name).parsed;
          else if (name instanceof ChrTok) key = ((ChrTok) name).parsed;
          else throw new SyntaxError("expected a key name, got " + name.toRepr(), name);
          List<Token> tokens = ct.tokens.subList(2, ct.tokens.size());
          
          Obj val = Main.oexec(LineTok.inherit(tokens), nsc);
          res.setStr(key, val);
        }
        return res;
      } else { // array
        Obj fo = Main.oexec(fst, sc);
        if (fo instanceof Value) { // value array
          Value[] vs = new Value[size];
          for (int i = 0; i < ts.size(); i++) {
            Obj o = i==0? fo : Main.oexec(ts.get(i), sc);
            if (!(o instanceof Value)) throw new DomainError("⋄-array contained " + o.humanType(true), o);
            vs[i] = (Value) o;
          }
          return Arr.create(vs);
        } else if (fo instanceof Fun) { // function array
          Obj[] os = new Obj[size];
          for (int i = 0; i < ts.size(); i++) {
            Obj o = i==0? fo : Main.oexec(ts.get(i), sc);
            if (!(o instanceof Fun)) throw new DomainError("function array contained " + o.humanType(true), o);
            os[i] = o;
          }
          return new FunArr(os);
        } else throw new DomainError("⋄-array contained " + fo.humanType(true), fo);
      }
    }
    if (t instanceof DfnTok) return UserDefined.of((DfnTok) t, sc);
    if (t instanceof BracketTok) return Brackets.of((BracketTok) t, sc);
    if (t instanceof BacktickTok) return new ArrFun((BacktickTok) t, sc);
    if (t instanceof BigTok) return ((BigTok) t).val;
    if (t instanceof ScopeTok) return new StrMap(sc);
    throw new NYIError("Unknown type: " + t.toRepr(), t);
  }
}