package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.HArr;
import APL.types.dimensions.DimMFn;
import APL.types.functions.Builtin;

public class ReverseBuiltin extends Builtin implements DimMFn {
  public ReverseBuiltin() {
    super("⌽", 0x001);
  }
  @Override
  public Obj call(Value w, int dim) {
    return ((HArr) w).reverseOn(-dim-1);
  }
  public Obj call(Value w) {
    if (!(w instanceof HArr)) return w;
    return ((HArr) w).reverseOn(w.rank-1);
  }
}