package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class FloorBuiltin extends Builtin {
  public FloorBuiltin() {
    super("⌊", 0x011);
  }
  public Obj call(Value w) {
    return numChr(Num::floor, Char::lower, w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> Num.min((Num)a, (Num)w), a0, w0);
  }
}