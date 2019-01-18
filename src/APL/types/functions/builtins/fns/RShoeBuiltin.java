package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class RShoeBuiltin extends Builtin {
  public RShoeBuiltin(Scope sc) {
    super("⊃", sc);
  }
  
  public Obj call(Value w) {
    if (w instanceof Primitive) return w;
    else if (w.ia == 0) throw new DomainError("⊃ on array with 0 elements", w);
    else return w.first();
  }
  
  public Obj call(Value a, Value w) {
    if (w instanceof APLMap) {
      APLMap map = (APLMap) w;
      
      if (a.rank > 1) {
        Value[] arr = new Value[a.ia];
        for (int i = 0; i < a.ia; i++) {
          arr[i] = (Value) map.getRaw(a.get(i));
        }
        return Arr.create(arr, a.shape);
      }
      return map.getRaw(a);
    }
    for (Value v : a) {
      w = w.at(v.asIntVec(), sc.IO);
    }
    return w;
  }
}