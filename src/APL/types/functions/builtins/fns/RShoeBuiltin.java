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
    for (Value v : a.arr) {
      w = w.at(v.toIntArr(this), this);
    }
    return w;
  }
}