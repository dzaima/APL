package APL;

import APL.errors.*;
import APL.tokenizer.Token;
import APL.tokenizer.types.*;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.dimensions.*;
import APL.types.functions.*;
import APL.types.functions.builtins.*;
import APL.types.functions.builtins.AbstractSet;
import APL.types.functions.builtins.dops.*;
import APL.types.functions.builtins.fns.*;
import APL.types.functions.builtins.mops.*;
import APL.types.functions.trains.*;
import APL.types.functions.userDefined.UserDefined;

import java.util.*;

import static APL.Main.*;

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
    for (int i = 0; i < Main.printlvl; i++) print("  ");
    Main.printdbg(args);
  }
  private LinkedList<Obj> done;
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
    done = new LinkedList<>();
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
        
        if (Main.debug) printlvl("dotnot", Arrays.toString(names));
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
        if (Main.debug) printlvl(done);
        
      } else {
        c = valueOf(t);
      }
      if (c.isObj() || c.type() == Type.gettable && (left.size() == 0 || !(left.get(0) instanceof SetTok))) {
        if (arr == null) arr = new ArrayList<>();
        arr.add(c);
      } else {
        if (arr != null) {
          if (arr.size() == 1) done.addFirst(arr.get(0));
          else done.addFirst(VarArr.of(arr));
          update(false);
          arr = null;
        }
        done.addFirst(c);
        update(false);
      }
    }
    if (arr != null) {
      if (arr.size() == 1) done.addFirst(arr.get(0));
      else done.addFirst(VarArr.of(arr));
    }
    update(true);
    
    
//    if (done.size() != 1) update(done, true); // e.g. for f←1+
    
    Main.printlvl--;
    if (Main.debug) printlvl("END:", done);
    if (done.size() != 1) {
      if (done.size() == 0) return null;
      if (done.get(0).token != null) Main.faulty = done.get(0).token;
      // try to figure out what went wrong
  
      for (int i = done.size()-1; i >= 0; i--) {
        Obj obj = done.get(i);
        if (obj instanceof Variable) {
          Variable vobj = (Variable) obj;
          if (vobj.getOrThis() == obj) throw new SyntaxError("Couldn't find the value of " + vobj.name, obj);
        } else if (obj instanceof Settable) {
          Settable settable = (Settable) obj;
          if (settable.getOrThis() == obj) throw new SyntaxError("Couldn't find the value of " + obj, obj);
        }
      }
      
      // oh well that failed
      throw new SyntaxError("couldn't join everything up into a single expression", done.get(done.size()-1));
    }
    return done.get(0);
  }
  
  private void update(boolean end) {
    if (done.size() == 1 && done.get(0) == null) return;
    while (true) {
      if (Main.debug) printlvl(done);
      if (done.size() >= 3 && done.getFirst() instanceof JotBuiltin && done.get(1) instanceof DotBuiltin) {
        if (Main.debug) printlvl("∘.");
        var jot = done.removeFirst();
        done.removeFirst();
        var fn = done.removeFirst();
        if (fn instanceof Settable) fn = ((Settable) fn).get();
        if (fn instanceof VarArr) fn = ((VarArr) fn).get();
        var TB = new TableBuiltin();
        TB.token = jot.token;
        done.addFirst(TB.derive(fn));
        continue;
      }
      if (is("D!|NFN", end, false)) {
        if (Main.debug) printlvl("NFN");
        var w = lastVal();
        var f = lastFun();
        var a = lastVal();
        Main.faulty = f;
        var res = f.call(a, w);
        if (res == null && (left.size() > 0 || done.size() > 0)) throw new SyntaxError("trying to use result of function which returned nothing", a);
        if (res != null) done.addLast(res);
        else return;
        continue;
      }
      if (is("F@", end, true)) {
        if (Main.debug) printlvl("F[]");
        var f = (Fun) firstObj();
        var w = (Brackets) done.removeFirst();
        done.addFirst(new DervDimFn(f, w.toInt(), sc));
        continue;
      }
      if (is("M@", end, true)) {
        if (Main.debug) printlvl("M[]");
        var f = firstMop();
        var w = (Brackets) done.removeFirst();
        done.addFirst(new DervDimMop(f, w.toInt(), sc));
        continue;
      }
      if (is("D@", end, true)) {
        if (Main.debug) printlvl("D[]");
        var f = firstDop();
        var w = (Brackets) done.removeFirst();
        done.addFirst(new DervDimDop(f, w.toInt(), sc));
        continue;
      }
      if (is("v@", end, true)) {
        if (Main.debug) printlvl("v[]");
        var f = firstVar();
        var w = (Brackets) done.removeFirst();
        done.addFirst(new Pick((Variable) f, w, sc));
        continue;
      }
      if (is("[FM←]|FN", end, false)) {
        if (Main.debug) printlvl("FN");
        var w = lastVal();
        var f = lastFun();
        Main.faulty = f;
        var res = f.call(w);
        if (res == null && (left.size() > 0 || done.size() > 0)) throw new SyntaxError("trying to use result of function which returned nothing", f);
        if (res != null) done.addLast(res);
        else return;
        continue;
      }
      if (is("#!←", end, true) || done.size() == 1 && done.get(0).type() == Type.gettable) {
        var w = firstVar();
        addFirst(w.get());
      }
      if (is("D!|V←[#NFMD],#←[#NFMD],D!|D←D,D!|M←M,D!|F←F,D!|N←N", end, false)) { // "D!|.←." to allow changing type
        if (Main.debug) printlvl("N←.");
        var w = lastObj();
        var s = (AbstractSet) done.removeLast(); // ←
        var a = done.removeLast(); // variable
        Main.faulty = s;
        var res = s.call(a, w, false);
        done.addLast(res);
        continue;
      }
      if (done.size() == 2 && is("F←", false, false)) {
        if (Main.debug) printlvl("F←");
        var s0 = done.removeLast(); // ←
        if (s0 instanceof DerivedSet) throw new SyntaxError("cannot derive an already derived ←");
        var s = (SetBuiltin) s0;
        var f = lastFun();
        done.addLast(new DerivedSet(s, f));
        continue;
      }
      if (is("D!|NF←N", end, false)) {
        if (Main.debug) printlvl("NF←.");
        var w = lastVal();
        var s0 = done.removeLast(); // ←
        if (s0 instanceof DerivedSet) throw new SyntaxError("cannot derive an already derived ←");
        var s = (SetBuiltin) s0;
        var f = lastFun();
        Obj a = done.removeLast(); // variable
        Main.faulty = f;
        var res = s.call(f, a, w);
        if (res != null) done.addLast(res);
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
        var aa = done.remove(barPtr); // done.removeFirst();
        var  o = firstDop(); // (Dop) done.removeFirst();
        var ww = done.remove(barPtr);
        var aau = aa;
        var wwu = ww;
        if (aau instanceof Settable) aau = ((Settable) aau).getOrThis();
        if (wwu instanceof Settable) wwu = ((Settable) wwu).getOrThis();
        if (aau instanceof VarArr) aau = ((VarArr) aau).get();
        if (wwu instanceof VarArr) wwu = ((VarArr) wwu).get();
        if (o instanceof DotBuiltin && aau instanceof APLMap && ww instanceof Variable) {
          done.add(barPtr, ((APLMap) aau).get(Main.toAPL(((Variable) ww).name)));
        } else {
          done.add(barPtr, o.derive(aau, wwu));
        }
        continue;
      }
      if (is("D!|[FN]FF", end, false)) {
        if (Main.debug) printlvl("f g h");
        var h = lastObj();
        var g = lastFun();
        var f = lastObj();
        done.addLast(new Fork(f, g, h));
        continue;
      }
      if (is("D!|NF", false, false)) {
        if (Main.debug) printlvl("A f");
        var f = lastFun();
        var a = lastObj();
        done.addLast(new Atop(a, f));
        continue;
      }
      if (is("←FF", false, false)) {
        if (Main.debug) printlvl("g h");
        var h = lastFun();
        var g = lastObj();
        done.addLast(new Atop(g, h));
        continue;
      }
      break;
    }
    if (end && done.size() == 2) {
      var h = lastFun();
      var g = lastObj();
      if (g instanceof Fun || g instanceof Value) done.addLast(new Atop(g, h));
      else throw new SyntaxError("creating an atop with "+g.humanType(true), g);
    }
  }
  
  private Value lastVal() {
    var r = done.removeLast();
    if (r instanceof Value) return (Value) r;
    if (r instanceof VarArr) return ((VarArr) r).get();
    if (r instanceof Settable) return (Value) ((Settable) r).get();
    throw new SyntaxError("Expected value, got "+r, r);
  }
  
  private Fun lastFun() {
    var r = done.removeLast();
    if (r instanceof Fun) return (Fun) r;
    if (r instanceof Settable) return (Fun) ((Settable) r).get();
    throw new SyntaxError("Expected function, got "+r, r);
  }
  
  
  private Dop firstDop() {
    var r = done.remove(barPtr);
    if (r instanceof Dop) return (Dop) r;
    if (r instanceof Settable) return (Dop) ((Settable) r).get();
    throw new SyntaxError("Expected dop, got "+r, r);
  }
  
  private Obj lastObj() {
    var r = done.removeLast();
    if (r instanceof VarArr) return ((VarArr) r).get();
    if (r instanceof Settable) return ((Settable) r).get();
    return r;
  }
  private Obj firstObj() {
    var r = done.remove(barPtr);
    if (r instanceof VarArr) return ((VarArr) r).get();
    if (r instanceof Settable) return ((Settable) r).get();
    return r;
  }
  private Settable firstVar() {
    var r = done.remove(barPtr);
    if (r instanceof Settable) return (Settable) r;
    throw new SyntaxError("Expected a variable, got "+r, r);
  }
  private Mop firstMop() {
    var r = done.remove(barPtr);
    if (r instanceof Mop) return (Mop) r;
    if (r instanceof Settable) return (Mop) ((Settable) r).get();
    throw new SyntaxError("Expected mop, got "+r, r);
  }
  private void addFirst(Obj o) {
    done.add(barPtr, o);
  }
  
  
  private int barPtr = 0;
  
  private boolean is(String pt, boolean everythingDone, boolean fromStart) {
    barPtr = 0;
    if (pt.contains(",")) {
      for (String s : pt.split(",")) {
        if (is(s, everythingDone, fromStart)) return true;
      }
    }
    if (everythingDone && is(pt, false, fromStart)) return true;
    if (fromStart && everythingDone && pt.contains("|")) {
      return is(pt.split("\\|")[1], false, true);
    }
    int len = pt.length();
    int ptr = fromStart ? 0 : done.size() - 1;
    int ptrinc = fromStart ? 1 : -1;
    boolean pass = false;
    for (int i = fromStart ? 0 : len - 1; (fromStart ? i < len : i >= 0); i += ptrinc) {
      char p = pt.charAt(i);
      String any;
      boolean inv = false;
      if (p == '|') {
        pass = everythingDone;
        barPtr = ptr;
        i += ptrinc;
        p = pt.charAt(i);
      }
      if (ptr >= done.size() || ptr < 0) return pass;
      if (p == '!') {
        inv = true;
        i += ptrinc;
        p = pt.charAt(i);
      }
      if (p == ']') { // regular
        int si = i;
        while (pt.charAt(i) != '[') i--;
        any = pt.substring(i + 1, si);
        } else if (p == '[') { // reverse
        int si = i;
        while (pt.charAt(i) != ']') i++;
        any = pt.substring(si + 1, i);
      } else any = String.valueOf(p);
      if (p == '.') {
        ptr += ptrinc;
        continue;
      }
      Obj v = done.get(ptr);
      if (p == 'v') {
        if (!(v instanceof Settable) ^ inv) return false;
        ptr += ptrinc;
        continue;
      }
      char type;
      switch (v.type()) {
        case array:
          type = 'N';
          break;
        case fn:
        case bfn:
          type = 'F';
          break;
        case set:
          type = '←';
          break;
        case mop:
        case bmop:
          type = 'M';
          break;
        case dop:
        case bdop:
          type = 'D';
          break;
        case var:
        case nul:
          type = 'V';
          break;
        case dim:
          type = '@';
          break;
        case gettable:
          type = '#';
          break;
        default:
          throw up;
      }
      if ((!any.contains(String.valueOf(type))) ^ inv) return false;
      ptr += ptrinc;
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
        // slashes: / - reduce; ⌿ - replicate; \ - reduce (r[3]←(r[2] ← (r[1]←a) f b) f c); ⍀ - extend? (todo)
        // in Dyalog but not at least partially implemented: ⊆⌹→  &⌶⌺⍤
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
        case '↑': return new UpArrowBuiltin(sc);
        case '↓': return new DownArrowBuiltin(sc);
        case '?': return new RandBuiltin(sc);
        case '⍪': return new CommaBarBuiltin();
        case '⍉': return new TransposeBuiltin();
        case '⊖': return new FlipBuiltin();
        case '⌽': return new ReverseBuiltin();
        case 'ϼ': return new RhoBarBuiltin(sc);
        
        case '…': return new EllipsisBuiltin();
        case '⍮': return new SemiUBBuiltin();
        case '⍕': return new FormatBuiltin();
        case '⍎': return new EvalBuiltin(sc);
        case '⍋': return new GradeUpBuiltin(sc);
        case '⍒': return new GradeDownBuiltin(sc);
        case '⌿': return new ReplicateBuiltin();
        case '⍧': return new LShoeStileBuiltin();
        
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
        case '⍄': return new EachLeft();
        case '⍃': return new EachRight();
        
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
        case '⍫': return new ObserveBuiltin();
        
        
        case '⍬': return new DoubleArr(new double[0]);
        case '⎕': return new Quad(sc);
        case '⍞': return new QuoteQuad();
        case '⍺': Obj o = sc.get("⍺"); if(o == null) throw new SyntaxError("No ⍺ found"); return o;
        case '⍵':     o = sc.get("⍵"); if(o == null) throw new SyntaxError("No ⍵ found"); return o;
        case '∇':     o = sc.get("∇"); if(o == null) throw new SyntaxError("No ∇ found"); return o;
        case '⍶':     o = sc.get("⍶"); if(o == null) throw new SyntaxError("No ⍶ found"); return o;
        case '⍹':     o = sc.get("⍹"); if(o == null) throw new SyntaxError("No ⍹ found"); return o;
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
      if (ts.size() == 0) return new StrMap();
      LineTok fst = ts.get(0);
      if (fst.tokens != null && fst.colonPos() != -1) {
        StrMap map = new StrMap();
        for (LineTok ct : ts) {
          Token name = ct.tokens.get(0);
          if (ct.colonPos() ==-1)         SyntaxError.direct("expected a colon in expression", ct.tokens.get(0));
          if (ct.colonPos() != 1)         SyntaxError.direct("expected : to be the 2nd token in parenthesis", ct.tokens.get(ct.colonPos()));
          if (!(name instanceof NameTok)) SyntaxError.direct("expected a key name, got " + Main.explain(name), name);
          List<Token> tokens = ct.tokens.subList(2, ct.tokens.size());
          //noinspection ConstantConditions no?
          map.setStr(((NameTok) name).name, Main.oexec(LineTok.inherit(tokens), sc));
        }
        return map;
      } else {
        if (ts.size() == 1) {
         return Main.exec(fst, sc);
        }
        List<Token> sts = new ArrayList<>();
        for (Token st : ((ParenTok) t).tokens) {
          sts.add(LineTok.inherit(st));
        }
        return Main.exec(LineTok.inherit(sts), sc); // todo think about whether this should be a thing
      }
    }
    if (t instanceof DfnTok) return UserDefined.of((DfnTok) t, sc);
    if (t instanceof BracketTok) return new Brackets((BracketTok) t, sc);
    if (t instanceof BacktickTok) return new ArrFun((BacktickTok) t, sc);
    throw new NYIError("Unknown type: " + Main.explain(t), t);
  }
}