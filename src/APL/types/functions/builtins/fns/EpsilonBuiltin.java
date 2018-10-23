package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;


public class EpsilonBuiltin extends Builtin {
  public EpsilonBuiltin() {
    super("∊", 0x011);
  }
  
  public Obj call(Value w) {
    var res = new ArrayList<Value>();
    rec(res, w);
    return new Arr(res);
  }
  
  private void rec(ArrayList<Value> arr, Value v) {
    if (v instanceof Arr) {
      for (Value c : v.arr) rec(arr, c);
    } else arr.add(v);
  }
  
  public Obj call(Value a, Value w) {
    Value[] res = new Value[a.ia];
    for (int i = 0; i < a.ia; i++) {
      Value av = a.arr[i];
      res[i] = Arrays.stream(w.arr).anyMatch(v -> v.equals(av))? Num.ONE : Num.ZERO;
    }
    return new Arr(res, a.shape);
  }
}