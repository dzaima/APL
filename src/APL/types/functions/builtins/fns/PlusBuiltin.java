package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.functions.Builtin;
import APL.types.*;

public class PlusBuiltin extends Builtin {
  public PlusBuiltin() {
    super("+");
    valid = 0x011;
  }

  public Obj call(Value w) { return vec(w); }
  public Obj call(Value a, Value w) { return vec(a, w); }

  protected Value scall(Value w) {
     if (!(w instanceof Num)) throw new DomainError("Conjugating a non-number"); // TODO decide whether this should exist
    return ((Num)w).conjugate();
  }
  protected Value scall(Value a, Value w) {
    return ((Num)a).plus((Num)w);
  }
}