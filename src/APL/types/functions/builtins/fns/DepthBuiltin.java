package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class DepthBuiltin extends Builtin {
  @Override public String repr() {
    return "≡";
  }
  
  
  public static int lazy(Value w) {
    int depth = 0;
    while (!(w instanceof Primitive)) {
      w = w.first();
      depth++;
    }
    return depth;
  }
  public static int full(Value w) {
    if (w instanceof Primitive) return 0;
    if (w instanceof DoubleArr) return 1;
    if (w instanceof ChrArr) return 1;
    boolean first = true;
    boolean uneven = false;
    int sub = 0;
    for (Value v : w) {
      int cd = full(v);
      if (cd < 0) {
        uneven = true;
        cd = -cd;
      }
      if (first) {
        first = false;
        sub = cd;
      } else if (sub != cd) {
        sub = Math.max(sub, cd);
        uneven = true;  
      }
    }
    sub++;
    return uneven? -sub : sub;
  }
  
  public Obj call(Value w) {
    return Num.of(full(w));
  }
  public Obj call(Value a, Value w) {
    return a.equals(w)? Num.ONE : Num.ZERO;
  }
}