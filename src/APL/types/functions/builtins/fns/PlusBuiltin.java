package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.functions.Builtin;
import APL.types.*;


public class PlusBuiltin extends Builtin {
  public PlusBuiltin() {
    super("+");
    valid = 0x011;
  }

  public Obj call(Value w) {
    return scalar(v -> {
      if (!(w instanceof Num)) throw new DomainError("Conjugating a non-number", this, w); // TODO decide whether this should exist
      return ((Num)w).conjugate();
    }, w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> {
      if (!(w instanceof Num)) throw new DomainError("+ on non-number ⍵", this, w);
      if (!(a instanceof Num)) throw new DomainError("+ on non-number ⍺", this, a);
      return ((Num)a).plus((Num)w);
    }, a0, w0);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) {
    try {
      return new MinusBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError("", this, e.cause);
    }
  }
}