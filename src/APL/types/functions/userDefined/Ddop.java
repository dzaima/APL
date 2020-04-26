package APL.types.functions.userDefined;

import APL.*;
import APL.errors.DomainError;
import APL.tokenizer.types.DfnTok;
import APL.types.*;
import APL.types.functions.*;

import static APL.Main.printdbg;


public class Ddop extends Dop {
  public final DfnTok code;
  
  @Override public String repr() {
    return code.toRepr();
  }
  
  Ddop(DfnTok t, Scope sc) {
    super(sc);
    code = t;
  }
  
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    Obj o = callObj(aa, ww, w, derv);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Obj aa, Obj ww, Value w, DerivedDop derv) {
    printdbg("ddop call", w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍹", ww);
    nsc.set("⍺", new Variable(nsc, "⍺"));
    nsc.set("⍵", w);
    nsc.set("∇", derv);
    var res = Main.execLines(code, nsc);
    if (res instanceof VarArr) return ((VarArr)res).get();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    Obj o = callObj(aa, ww, a, w, derv);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    printdbg("ddop call", a, w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍹", ww);
    nsc.set("⍺", a);
    nsc.set("⍵", w);
    nsc.set("∇", derv);
    nsc.alphaDefined = true;
    var res = Main.execLines(code, nsc);
    if (res instanceof VarArr) return ((VarArr)res).get();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
}