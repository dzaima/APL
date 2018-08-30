package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class DivBuiltin extends Builtin {
  public DivBuiltin() {
    super("÷");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return scalar(v -> Num.ONE.divide((Num) w), w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> {
      if (!(a instanceof Num)) throw new DomainError("non-number ⍺ argument to -", this, a);
      if (!(w instanceof Num)) throw new DomainError("non-number ⍵ argument to -", this, w);
      return ((Num)a).divide((Num)w);
    }, a0, w0);
  }
  
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) { return call(a, w); }
}
