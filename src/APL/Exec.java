package APL;

import java.util.*;
import APL.types.*;
import APL.errors.*;
import APL.types.functions.*;
import APL.types.functions.builtins.*;
import APL.types.functions.builtins.fns.*;
import APL.types.functions.builtins.mops.*;
import APL.types.functions.builtins.dops.*;
import APL.types.functions.trains.*;
import APL.types.functions.userDefined.UserDefined;

import static APL.Main.*;

class Exec {
  private Scope sc;
  private List<Token> tokens;
  
  Exec(Token ln, Scope sc) {
    assert (ln.type == TType.expr || ln.type == TType.usr);
    tokens = ln.tokens;
    this.sc = sc;
  }

  private void printlvl(Object... args) {
    if (!Main.debug) return;
    for (int i = 0; i < Main.printlvl; i++) print("  ");
    Main.printdbg(args);
  }
  private LinkedList<Obj> done;
  private Stack<Token> left;
  Obj exec() {
    if (sc.alphaDefined && tokens.size() >= 2 && "⍺".equals(tokens.get(0).repr) && tokens.get(1).type == TType.set) {
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
      Obj c = valueOf(t);
      // if (Main.debug) printlvl("token", t.toRepr(), "-> object", c);
      if (c.isObj()) {
        if (arr == null) arr = new ArrayList<>();
        arr.add(c);
      } else {
        if (arr != null) {
          if (arr.size() == 1) done.addFirst(arr.get(0));
          else done.addFirst(new VarArr(arr));
          update(false);
          arr = null;
        }
        if (t.type == TType.op) done.addFirst(c);
        else if (t.type == TType.set) done.addFirst(c);
        else if (t.type == TType.name) done.addFirst(c);
        else if (t.type == TType.expr) done.addFirst(c);
        else if (t.type == TType.usr) done.addFirst(c);
        else throw new Error("unknown type: " + t.type + " (no idea if this should be a thing)");
        update(false);
      }
    }
    if (arr != null) {
      if (arr.size() == 1) done.addFirst(arr.get(0));
      else done.addFirst(new VarArr(arr));
    }
    update(true);
    
    
//    if (done.size() != 1) update(done, true); // e.g. for f←1+
    
    Main.printlvl--;
    if (Main.debug) printlvl("END:", done);
    if (done.size() != 1) throw new SyntaxError("couldn't join everything up into a single expression");
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
        var res = f.call(a, w);
        if (res == null && left.size() > 0) throw new SyntaxError("trying to use result of function which returned null");
        done.addLast(res);
        if (res == null) return;
        continue;
      }
      if (is("[FM←]|FN", end, false)) {
        if (Main.debug) printlvl("FN");
        var w = lastVal();
        var f = lastFun();
        var res = f.call(w);
        if (res == null && left.size() > 0) throw new SyntaxError("trying to use result of function which returned null");
        done.addLast(res);
        if (res == null) return;
        continue;
      }
      if (is("D!|V←.,D!|D←D,D!|M←M,D!|F←F,D!|N←N", end, false)) { // "D!|.←." to allow changing type
        if (Main.debug) printlvl("N←.");
        var w = lastObj();
        var s = (SetBuiltin) done.removeLast(); // ←
        var a = done.removeLast(); // variable
        done.addLast(s.call(a, w, false));
        continue;
      }
      if (is("D!|NF←N", end, false)) {
        if (Main.debug) printlvl("NF←.");
        var w = lastVal();
        var s = (SetBuiltin) done.removeLast(); // ←
        var f = lastFun();
        Obj a = done.removeLast(); // variable
        done.addLast(s.call(f, a, w));
        continue;
      }
      if (is("!D|[FN]M", end, true)) {
        if (Main.debug) printlvl("FM");
        var f = firstObj();
        var o = firstMop();
        addFirst(o.derive(f));
        continue;
      }
      if (is("[FNV]D[FNV]", end, true)) {
        if (Main.debug) printlvl("FDF");
        var aa = done.removeFirst(); // done.removeFirst();
        var  o = firstDop(); // (Dop) done.removeFirst();
        var ww = done.removeFirst();
        var aau = aa;
        var wwu = ww;
        if (aau instanceof Settable) aau = ((Settable) aau).getOrThis();
        if (wwu instanceof Settable) wwu = ((Settable) wwu).getOrThis();
        if (o instanceof DotBuiltin && aau instanceof APLMap && ww instanceof Variable) {
          done.addFirst(((APLMap) aau).get(Main.toAPL(((Variable) ww).name, ww.token)));
        } else {
          done.addFirst(o.derive(aau, wwu));
        }
        continue;
      }
      if (is(".|[FN]FF", end, false)) {
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
      done.addLast(new Atop(g, h));
    }
  }
  
  private Value lastVal() {
    var r = done.removeLast();
    if (r instanceof Value) return (Value) r;
    if (r instanceof VarArr) return ((VarArr) r).materialize();
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
    var r = done.removeFirst();
    if (r instanceof Dop) return (Dop) r;
    if (r instanceof Settable) return (Dop) ((Settable) r).get();
    throw new SyntaxError("Expected function, got "+r, r);
  }
  
  private Obj lastObj() {
    var r = done.removeLast();
    if (r instanceof VarArr) return ((VarArr) r).materialize();
    if (r instanceof Settable) return ((Settable) r).get();
    return r;
  }
  private Obj firstObj() {
    var r = done.remove(barPtr);
    if (r instanceof VarArr) return ((VarArr) r).materialize();
    if (r instanceof Settable) return ((Settable) r).get();
    return r;
  }
  private Mop firstMop() {
    var r = done.remove(barPtr);
    if (r instanceof Mop) return (Mop) r;
    if (r instanceof Settable) return (Mop) ((Settable) r).get();
    throw new SyntaxError("Expected function, got "+r, r);
  }
  private void addFirst(Obj o) {
    done.add(barPtr, o);
  }
  
//  private Obj lastObj() {
//    var r = done.removeLast();
//    if (r instanceof VarArr) return ((VarArr) r).materialize();
//    if (r instanceof Settable) return ((Settable) r).get();
//    return r;
//  }
  
  private int barPtr = 0;

  private boolean is(String pt, boolean everythingDone, boolean reverse) {
    barPtr = 0;
    if (pt.contains(",")) {
      for (String s : pt.split(",")) {
        if (is(s, everythingDone, reverse)) return true;
      }
    }
    if (everythingDone && is(pt, false, reverse)) return true;
    if (reverse && everythingDone && pt.contains("|")) {
      return is(pt.split("\\|")[1], false, true);
    }
    int len = pt.length();
    int ptr = reverse ? 0 : done.size() - 1;
    int ptrinc = reverse ? 1 : -1;
    boolean pass = false;
    for (int i = reverse ? 0 : len - 1; (reverse ? i < len : i >= 0); i += ptrinc) {
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
//        i--;
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
        case var: case nul:
          type = 'V';
          break;
        default:
          throw up;
      }
//      printdbg(type, v);
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
    switch (t.type) {
      case op:
        switch (t.repr.charAt(0)) {
        
          //TODO in Dyalog but not here: ⌽⊖⊆∊⍷⌿\⍀∩∪⌹→  ⌿\⍀&⌶⍠⌸⌺⍤@
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
          
          case '⊂': return new LShoeBuiltin();
          case '⊇': return new RShoeUBBuiltin(sc);
          case '⊃': return new RShoeBuiltin(sc);
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
          
          case '…': return new EllipsisBuiltin();
          case '⍕': return new FormatBuiltin();
          case '⍎': return new EvalBuiltin(sc);
          case '⍋': return new GradeUpBuiltin(sc);
          case '⍒': return new GradeDownBuiltin(sc);
          
          // comparisons
          case '<': return new LTBuiltin();
          case '≤': return new LEBuiltin();
          case '=': return new EQBuiltin();
          case '≥': return new GEBuiltin();
          case '>': return new GTBuiltin();
          case '≠': return new NEBuiltin();
  
          // mops
          case '/': return new ReduceBuiltin();
          case '¨': return new EachBuiltin();
          case '⍨': return new SelfieBuiltin();
          case '⌾': return new TableBuiltin();
          case '⌸': return new KeyBuiltin(sc);
  
          // dops
          case '∘': return new JotBuiltin();
          case '.': return new DotBuiltin();
          case '⍣': return new RepeatBuiltin();
          case '⍥': return new OverBuiltin();
          case '⍢': return new DualBuiltin();
  
  
          case '⍬': return new Arr(Num.ZERO);
          case '⎕': return new Logger(sc);
          case '⍺': return sc.get("⍺");
          case '⍵': return sc.get("⍵");
          case '⍶': return sc.get("⍶");
          case '⍹': return sc.get("⍹");
          default: throw new NYIError("no built-in " + t.repr + " defined in exec");
        }
      case number: return new Num(t.repr);
      case chr:    return t.repr.length() == 1? new Char(t.repr) : Main.toAPL(t.repr, t);
      case str:    return                                          Main.toAPL(t.repr, t);
      case set:    return new SetBuiltin();
      case name:   return sc.getVar(t.repr);
      case expr:   return Main.execTok(t, sc);
      case usr:    return UserDefined.of(t, sc);
      default: throw new NYIError("Unknown type: " + t.type);
    }
  }
}