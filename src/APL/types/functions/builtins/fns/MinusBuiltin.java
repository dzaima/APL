package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class MinusBuiltin extends Builtin {
  public MinusBuiltin() {
    super("-", 0x011);
  }
  
  public Obj call(Value w) {
    return numChr(Num::negate, Char::swap, w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> {
      if (!(a instanceof Num)) throw new DomainError("- on non-number ⍵", a);
      if (!(w instanceof Num)) throw new DomainError("- on non-number ⍺", w);
      return ((Num)a).minus((Num)w);
    }, a0, w0);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) { return call(a, w); }
}