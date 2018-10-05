package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class AndBuiltin extends Builtin {
  public AndBuiltin() {
    super("∧");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    Num[] nums = new Num[w.ia];
    for (int i = 0; i < w.ia; i++)  {
      nums[i] = (Num) w.arr[i];
    }
    return Num.lcm(nums);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> Num.lcm(new Num[]{(Num) a, (Num) w}), a0, w0);
  }
}