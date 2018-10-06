package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class StileBuiltin extends Builtin {
  public StileBuiltin() {
    super("|");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return numChr(Num::abs, c -> c, w); // TODO char something or remove
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> ((Num)w).mod((Num) a), a0, w0);
  }
}