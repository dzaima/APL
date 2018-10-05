package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class AndBuiltin extends Builtin {
  public AndBuiltin() {
    super("∧");
    valid = 0x010;
  }
  
//  public Obj call(Value w) {
//
//  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> ((Num) a).lcm((Num) w), a0, w0);
  }
}