package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;

public class RShoeBuiltin extends Builtin {
  public RShoeBuiltin(Scope sc) {
    super("⊃");
    this.sc = sc;
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    if (w.primitive()) return w;
    else if (w.ia == 0) return w.prototype;
    else return w.first();
  }
  
  public Obj call(Value a, Value w) {
    return w.at(a.toIntArr(this), this);
  }
}