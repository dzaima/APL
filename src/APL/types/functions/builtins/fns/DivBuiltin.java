package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class DivBuiltin extends Builtin {
  public DivBuiltin() {
    super("÷");
    valid = 0x011;
  }

  public Obj call(Value w) { return vec(w); }
  public Obj callInv(Value w) { return vec(w); }
  public Obj callInvW(Value a, Value w) { return vec(a, w); }
  public Obj call(Value a, Value w) { return vec(a, w); }

  protected Value scall(Value w) {
    return Num.ONE.divide((Num) w);
  }
  protected Value scall(Value a, Value w) {
    if (!(a instanceof Num)) throw new DomainError("non-number ⍺ argument to -", this, a);
    if (!(w instanceof Num)) throw new DomainError("non-number ⍵ argument to -", this, w);
    return ((Num)a).divide((Num)w);
  }
}
