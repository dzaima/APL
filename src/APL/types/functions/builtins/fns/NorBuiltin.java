package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.functions.Builtin;

public class NorBuiltin extends Builtin {
  public NorBuiltin(Scope sc) {
    super("⍱", 0x010, sc);
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> (Main.bool(a, sc) | Main.bool(w, sc))? Num.ZERO : Num.ONE, a0, w0);
  }
  public Obj call(Value w) {
    for (int i = 0; i < w.ia; i++) {
      if (Main.bool(w.get(i), sc)) return Num.ZERO;
    }
    return Num.ONE;
  }
}