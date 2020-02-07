package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.ArrayList;


public class EpsilonBuiltin extends Builtin {
  @Override public String repr() {
    return "∊";
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
    if (a.scalar()) {
      Value a1 = a.first();
      for (Value v : w) {
        if (v.equals(a1)) {
          return Num.ONE;
        }
      }
      return Num.ZERO;
    }
    Value[] res = new Value[a.ia];
    for (int i = 0; i < a.ia; i++) {
      Value av = a.get(i);
      Num b = Num.ZERO;
      for (Value v : w) {
        if (v.equals(av)) {
          b = Num.ONE;
          break;
        }
      }
      res[i] = b;
    }
    return Arr.create(res, a.shape);
  }
}