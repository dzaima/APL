package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class MulBuiltin extends Builtin {
  public MulBuiltin() {
    super("×");
    valid = 0x011;
  }

  public Obj call(Value w) { return vec(w); }
  public Obj call(Value a, Value w) { return vec(a, w); }
  public Obj callInvW(Value a, Value w) {
    try {
      return new DivBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError("", this, e.cause);
    }
  }

  protected Value scall(Value w) {
    Num n = (Num) w;
    return Main.compareObj(w, Num.ZERO);
  }
  protected Value scall(Value a, Value w) {
    return ((Num)a).times((Num)w);
  }
}