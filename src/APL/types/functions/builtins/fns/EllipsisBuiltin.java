package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class EllipsisBuiltin extends Builtin {
  public EllipsisBuiltin() {
    super("…", 0x010);
  }
  
  public Obj call(Value a, Value w) {
    double[] arr = new double[((Num)w).minus((Num)a).abs().asInt()+1];
    double s = a.asDouble();
    Arrays.setAll(arr, i -> s+i);
    return new DoubleArr(arr);
  }
}