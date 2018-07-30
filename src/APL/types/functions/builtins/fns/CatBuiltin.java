package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import static APL.APL.up;

public class CatBuiltin extends Builtin {
  public CatBuiltin() {
    super(",");
    valid = 0x011;
  }
  public Obj call(Value w) {
    if (w.scalar()) return new Arr(new Value[]{w});
    return new Arr(((Arr)w).arr);
  }
  public Obj call(Value a, Value w) {
    throw up;
  }
}