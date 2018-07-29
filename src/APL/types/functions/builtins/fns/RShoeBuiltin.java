package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class RShoeBuiltin extends Builtin {
  public RShoeBuiltin() {
    super("⊃");
    valid = 0x011;
  }

  public Obj call(Value w) {
    if (w.primitive()) return w;
    else if (w.ia == 0) return w.prototype;
    else return w.arr[0];
  }
}