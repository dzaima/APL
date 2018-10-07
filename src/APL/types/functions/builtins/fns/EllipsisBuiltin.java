package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class EllipsisBuiltin extends Builtin {
  public EllipsisBuiltin() {
    super("…");
    valid = 0x010;
  }
  
  public Obj call(Value a, Value w) {
    Value[] arr = new Value[((Num)w).minus((Num)a).abs().intValue()+1];
    Arrays.setAll(arr, i -> ((Num) a).plus(new Num(i)));
    return new Arr(arr);
  }
}