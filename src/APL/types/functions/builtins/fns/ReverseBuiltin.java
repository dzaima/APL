package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class ReverseBuiltin extends Builtin {
  public ReverseBuiltin() {
    super("⌽");
    valid = 0x001;
  }
  
  public Obj call(Value w) {
    if (!(w instanceof Arr)) return w;
    return ((Arr) w).reverseOn(w.rank-1);
  }
}