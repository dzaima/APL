package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.*;

public class EvalBuiltin extends Builtin {
  @Override public String repr() {
    return "⍎";
  }
  
  public EvalBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    Obj o = callObj(w);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("⍎: was expected to return an array, got "+o.humanType(true), this);
  }
  public Obj callObj(Value w) {
    if (w instanceof ArrFun) return ((ArrFun) w).obj();
    return Main.exec(w.asString(), sc);
  }
  public Value call(Value a, Value w) {
    Obj o = callObj(a, w);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("⍎: was expected to return an array, got "+o.humanType(true), this);
  }
  public Obj callObj(Value a, Value w) {
    if (a instanceof ArrFun) {
      Obj obj = ((ArrFun) a).obj();
      if (!(obj instanceof Fun)) throw new DomainError("⍎: ⍺ must be `function, was "+obj.humanType(true), this);
      return ((Fun) obj).callObj(w);
    } else {
      throw new DomainError("⍎: Expected ⍺ to be an arrayified function", this, a);
    }
  }
}