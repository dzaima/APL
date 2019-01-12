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
    return Arr.create(res.toArray(new Value[0]));
  }
  
  private void rec(ArrayList<Value> arr, Value v) {
    if (v instanceof Primitive) {
      arr.add(v);
    } else {
      for (Value c : v) rec(arr, c);
    }
  }
  
  public Obj call(Value a, Value w) {
    Value[] res = new Value[a.ia];
    for (int i = 0; i < a.ia; i++) {
      Value av = a.get(i);
      res[i] = Arrays.stream(w.values()).anyMatch(v -> v.equals(av))? Num.ONE : Num.ZERO;
    }
    return Arr.create(res, a.shape);
  }
}