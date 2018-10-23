package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class EvalBuiltin extends Builtin {
  public EvalBuiltin(Scope sc) {
    super("⍎", 0x001, sc);
  }
  
  public Obj call(Value w) {
    return Main.exec(w.fromAPL(), sc);
  }
}