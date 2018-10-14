package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.dimensions.DimMFn;
import APL.types.functions.Builtin;

public class FlipBuiltin extends Builtin implements DimMFn {
  public FlipBuiltin() {
    super("⊖");
    valid = 0x001;
  }
  @Override
  public Obj call(Value w, int dim) {
    return ((Arr) w).reverseOn(dim);
  }
  public Obj call(Value w) {
    return ((Arr) w).reverseOn(0);
  }
}