package APL.types.functions.builtins.fns;

import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class LogBuiltin extends Builtin {
  public LogBuiltin() {
    super("⍟");
    valid = 0x11;
  }
  
  public Obj call(Value w) {
    return ((Num) w).log(Num.E);
  }
  
  public Obj call(Value a, Value w) {
    return ((Num) w).log((Num) a);
  }
}