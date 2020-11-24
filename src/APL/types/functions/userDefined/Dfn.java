package APL.types.functions.userDefined;

import APL.*;
import APL.errors.DomainError;
import APL.tokenizer.types.DfnTok;
import APL.types.*;
import APL.types.functions.VarArr;

public class Dfn extends Fun {
  public final DfnTok code;
  Dfn(DfnTok t, Scope sc) {
    super(sc);
    code = t;
  }
  public Value call(Value w) {
    Obj o = callObj(w);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Value w) {
    Main.printdbg("dfn call", w);
    Scope nsc = new Scope(sc);
    nsc.set("⍺", new Variable(nsc, "⍺"));
    nsc.set("⍵", w);
    nsc.set("∇", this);
    var res = Main.execLines(code, nsc);
    if (res instanceof VarArr) return ((VarArr)res).get();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  public Value call(Value a, Value w) {
    Obj o = callObj(a, w);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Value a, Value w) {
    Main.printdbg("dfn call", a, w);
    Scope nsc = new Scope(sc);
    nsc.set("⍺", a);
    nsc.set("⍵", w);
    nsc.set("∇", this);
    nsc.alphaDefined = true;
    var res = Main.execLines(code, nsc);
    if (res instanceof VarArr) return ((VarArr)res).get();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  public String repr() {
    return code.toRepr();
  }
  
  public String name() { return "dfn"; }
}