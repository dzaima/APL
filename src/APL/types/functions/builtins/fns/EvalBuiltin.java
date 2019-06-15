package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.DomainError;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.*;

public class EvalBuiltin extends Builtin {
  @Override public String repr() {
    return "⍎";
  }
  
  public EvalBuiltin(Scope sc) {
    super(sc);
  }
  
  public Obj call(Value w) {
    return Main.exec(w.asString(), sc);
  }
  public Obj call(Value a, Value w) {
    if (a instanceof ArrFun) {
      return ((ArrFun) a).fun().call(w);
    } else {
      throw new DomainError("Expected ⍺ of ⍎ to be an arrayified function", this, a);
    }
  }
}