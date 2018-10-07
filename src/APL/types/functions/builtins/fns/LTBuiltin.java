package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.Arrays;


public class LTBuiltin extends Builtin {
  public LTBuiltin() {
    super("<");
    valid = 0x010;
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> a.compareTo(w)< 0? Num.ONE : Num.ZERO, a0, w0);
  }
  
  public Obj call(Value w) {
    var order = w.gradeUp(this);
    return new Arr(Arrays.stream(order).map(integer -> w.arr[integer]).toArray(Value[]::new));
  }
}
