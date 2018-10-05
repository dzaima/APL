package APL.types.functions.builtins.fns;

import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class LogBuiltin extends Builtin {
  public LogBuiltin() {
    super("⍟");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return scalar(v -> ((Num) w).log(Num.E), w);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> ((Num) w).log((Num) a), a0, w0);
  }
}