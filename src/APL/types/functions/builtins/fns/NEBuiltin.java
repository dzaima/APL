package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;


public class NEBuiltin extends Builtin {
  public NEBuiltin() {
    super("≠");
    valid = 0x010;
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> a.compareTo(w)!=0? Num.ONE : Num.ZERO, a0, w0);
  }
}
