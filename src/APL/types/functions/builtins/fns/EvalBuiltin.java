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
  
  public Obj call(Value w) {
    if (w instanceof ArrFun) return ((ArrFun) w).obj();
    return Main.exec(w.asString(), sc);
  }
  public Obj call(Value a, Value w) {
    if (a instanceof ArrFun) {
      Obj obj = ((ArrFun) a).obj();
      if (!(obj instanceof Fun)) throw new DomainError("⍺ of ⍎ must be `function, was "+obj.humanType(true));
      return ((Fun) obj).call(w);
    } else {
      throw new DomainError("Expected ⍺ of ⍎ to be an arrayified function", this, a);
    }
  }
}