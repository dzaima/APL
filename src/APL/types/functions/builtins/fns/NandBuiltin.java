package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

public class NandBuiltin extends Builtin {
  @Override public String repr() {
    return "⍲";
  }
  
  public NandBuiltin(Scope sc) {
    super(sc);
  }
  
  public Obj call(Value a0, Value w0) {
    return allM((a, w) -> (Main.bool(a, sc) & Main.bool(w, sc))? Num.ZERO : Num.ONE, a0, w0);
  }
  public Obj call(Value w) {
    if (w instanceof DoubleArr) {
      double[] da = w.asDoubleArr();
      for (int i = 0; i < w.ia; i++) {
        if (!Main.bool(da[i], sc)) return Num.ONE;
      }
      return Num.ZERO;
    }
    for (int i = 0; i < w.ia; i++) {
      if (!Main.bool(w.get(i), sc)) return Num.ONE;
    }
    return Num.ZERO;
  }
}