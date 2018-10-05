package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class OrBuiltin extends Builtin {
  public OrBuiltin() {
    super("∨");
    valid = 0x010; // TODO gcd of array
  }
  
//  public Obj call(Value w) {
//
//  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> ((Num) a).gcd((Num) w), a0, w0);
  }
}