package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class ExclBuiltin extends Builtin {
  public ExclBuiltin() {
    super("!", 0x011);
  }
  
  public Obj call(Value w) {
    return scalar(v-> ((Num) v).fact(this), w);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> ((Num) w).binomial((Num) a, this), a0, w0);
  }
}