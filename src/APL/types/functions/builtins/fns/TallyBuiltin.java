package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class TallyBuiltin extends Builtin {
  public TallyBuiltin() {
    super("≢", 0x011);
  }
  public Obj call(Value w) {
    if (w.scalar()) return Num.ONE;
    return new Num(w.shape[0]);
  }
  public Obj call(Value a, Value w) {
    return a.equals(w)? Num.ZERO : Num.ONE;
  }
}