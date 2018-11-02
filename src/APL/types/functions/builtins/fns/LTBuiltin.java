package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.HArr;
import APL.types.functions.Builtin;

import java.util.Arrays;


public class LTBuiltin extends Builtin {
  public LTBuiltin() {
    super("<", 0x010);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> a.compareTo(w)< 0? Num.ONE : Num.ZERO, a0, w0);
  }
  
  public Obj call(Value w) {
    var order = w.gradeUp();
    Value[] res = new Value[order.length];
    Arrays.setAll(res, i -> w.get(order[i]));
    return new HArr(res);
//    return new HArr(Arrays.stream(order).map(w::get).toArray(Value[]::new)); TODO or this?
  }
}
