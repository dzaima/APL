package APL.types.functions.builtins.fns;

import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

import static APL.Main.compare;

public class EQBuiltin extends Builtin {
  public EQBuiltin() {
    super("=");
    valid = 0x010;
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> compare(a, w)==0? Num.ONE : Num.ZERO, a0, w0);
  }
}
