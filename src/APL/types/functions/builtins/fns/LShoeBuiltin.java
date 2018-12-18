package APL.types.functions.builtins.fns;

import APL.Main;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class LShoeBuiltin extends Builtin {
  public LShoeBuiltin() {
    super("⊂", 0x011);
  }

  public Obj call(Value w) {
    if (!Main.enclosePrimitives && w instanceof Primitive) return w;
    return new Rank0Arr(w);
  }
}
