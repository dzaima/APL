package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class LShoeBuiltin extends Builtin {
  public LShoeBuiltin() {
    super("⊂");
    valid = 0x011;
  }

  public Obj call(Value w) {
    if (w.primitive()) return w;
    return new Arr(new Value[]{w}, new int[0]);
  }
}
