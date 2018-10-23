package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.functions.Builtin;

public class NandBuiltin extends Builtin { // TODO monadic
  public NandBuiltin(Scope sc) {
    super("⍲", 0x010, sc);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> (Main.bool(a, sc) & Main.bool(w, sc))? Num.ZERO : Num.ONE, a0, w0);
  }
}