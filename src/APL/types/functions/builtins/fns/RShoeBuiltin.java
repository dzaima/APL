package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;

public class RShoeBuiltin extends Builtin {
  public RShoeBuiltin(Scope sc) {
    super("⊃");
    valid = 0x011;
    this.sc = sc;
  }
  
  public Obj call(Value w) {
    if (w.primitive()) return w;
    else if (w.ia == 0) return w.prototype;
    else return w.first();
  }
  
  public Obj call(Value a, Value w) {
    if (w instanceof APLMap) {
      APLMap map = (APLMap) w;
      
      if (a.rank > 1) {
        Value[] arr = new Value[a.ia];
        for (int i = 0; i < a.ia; i++) {
          arr[i] = (Value) map.getRaw(a.arr[i]);
        }
        return new Arr(arr, a.shape);
      }
      return map.getRaw(a);
    }
    for (Value v : a.arr) {
      w = w.at(v.toIntArr(this), this);
    }
    return w;
  }
}