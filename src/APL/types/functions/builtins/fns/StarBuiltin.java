package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class StarBuiltin extends Builtin {
  public StarBuiltin() {
    super("*", 0x011);
  }
  
  public Obj call(Value w) {
    return scalar(v -> Num.E.pow((Num) v), w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> ((Num)a).pow((Num)w), a0, w0);
  }
}