package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;
import static APL.Main.compare;

public class LTBuiltin extends Builtin {
  public LTBuiltin() {
    super("<");
    valid = 0x010;
  }
  
  
  public Obj call(Value w) {
    var order = w.gradeUp(this);
    Value[] res = new Value[order.length];
    for (int i = 0; i < order.length; i++) {
      res[i] = w.arr[order[i]];
    }
    return new Arr(res);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> compare(a, w)<0? Num.ONE : Num.ZERO, a0, w0);
  }
}
