package APL.types.functions.builtins.mops;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Mop;

public class ReduceBuiltin extends Mop {
  public ReduceBuiltin() {
    super("/");
    valid = 0x010;
  }

  public Obj call(Obj f, Value w) {
    // TODO ranks
    Value[] a = w.arr;
    if (a.length == 0) {
      if (((Fun)f).identity == null) throw new DomainError("No identity defined for "+f.name(), this, f);
      return ((Fun)f).identity;
    }
    Value last = a[a.length-1];
    for (int i = a.length-2; i >= 0; i--) {
      last = (Value)((Fun)f).call(a[i], last);
    }
    return last;
  }
}