package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class MinusBuiltin extends Builtin {
  public MinusBuiltin() {
    super("-");
    valid = 0x011;
  }
  public Obj call(Value w) { return vec(w); }
  public Obj call(Value a, Value w) { return vec(a, w); }

  protected Value scall(Value w) {
    return ((Num)w).negate();
  }
  protected Value scall(Value a, Value w) {
    return ((Num)a).minus((Num)w);
  }
}
