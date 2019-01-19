package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class DepthBuiltin extends Builtin {
  @Override public String repr() {
    return "≡";
  }
  
  
  public Obj call(Value w) {
    int depth = 0;
    while (!(w instanceof Primitive)) {
      w = w.first();
      depth++;
    }
    return new Num(depth);
  }
  public Obj call(Value a, Value w) {
    return a.equals(w)? Num.ONE : Num.ZERO;
  }
}