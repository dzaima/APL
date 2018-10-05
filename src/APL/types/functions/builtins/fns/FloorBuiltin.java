package APL.types.functions.builtins.fns;

import APL.types.Char;
import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class FloorBuiltin extends Builtin {
  public FloorBuiltin() {
    super("⌊");
    valid = 0x11;
  }
  public Obj call(Value w) {
    return numChr(Num::floor, Char::lower, w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> Num.min((Num)a, (Num)w), a0, w0);
  }
}