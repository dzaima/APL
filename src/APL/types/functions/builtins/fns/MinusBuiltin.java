package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class MinusBuiltin extends Builtin {
  public MinusBuiltin() {
    super("-");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return scalar(v -> {
      if (!(w instanceof Num)) throw new DomainError("negating a non-number", this, w); // TODO decide whether this should exist
      return ((Num)w).negate();
    }, w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> {
      if (!(a instanceof Num)) throw new DomainError("- on non-number ⍵", this, a);
      if (!(w instanceof Num)) throw new DomainError("- on non-number ⍺", this, w);
      return ((Num)a).minus((Num)w);
    }, a0, w0);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) { return call(a, w); }
}