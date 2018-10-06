package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class RootBuiltin extends Builtin {
  public RootBuiltin() {
    super("√");
    valid = 0x11;
  }
  
  public Obj call(Value w) {
    return scalar(v -> ((Num) v).root(Num.TWO), w);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> ((Num) w).root((Num) a), a0, w0);
  }
}