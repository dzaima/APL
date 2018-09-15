package APL.types.functions.builtins.fns;

import APL.types.Arr;
import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class EllipsisBuiltin extends Builtin {
  public EllipsisBuiltin() {
    super("…");
    valid = 0x010;
  }
  
  public Obj call(Value a, Value w) {
    Value[] arr = new Value[((Num)w).minus((Num)a).abs().intValue()+1];
    for (int i = 0; i < arr.length; i++) arr[i] = ((Num)a).plus(new Num(i));
    return new Arr(arr);
  }
}