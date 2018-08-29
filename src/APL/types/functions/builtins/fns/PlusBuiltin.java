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
  public Obj callInv(Value w) { return vec(w); }
  public Obj call(Value a, Value w) { return vec(a, w); }
  public Obj callInvW(Value a, Value w) {
    try {
      return new MinusBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError("", this, e.cause);
    }
  }

  protected Value scall(Value w) {
    if (!(w instanceof Num)) throw new DomainError("Conjugating a non-number", this, w); // TODO decide whether this should exist
    return ((Num)w).conjugate();
  }
  protected Value scall(Value a, Value w) {
    if (!(w instanceof Num)) throw new DomainError("+ on non-number ⍵", this, w);
    if (!(a instanceof Num)) throw new DomainError("+ on non-number ⍺", this, a);
    return ((Num)a).plus((Num)w);
  }
}