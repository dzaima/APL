package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.dimensions.DimMFn;
import APL.types.functions.Builtin;

public class ReverseBuiltin extends Builtin implements DimMFn {
  public ReverseBuiltin() {
    super("⌽", 0x001);
  }
  @Override
  public Obj call(Value w, int dim) {
    return ((Arr) w).reverseOn(-dim-1);
  }
  public Obj call(Value w) {
    if (!(w instanceof Arr)) return w;
    return ((Arr) w).reverseOn(w.rank-1);
  }
}