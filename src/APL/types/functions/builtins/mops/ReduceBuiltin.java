package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.functions.Mop;

public class ReduceBuiltin extends Mop {
  public ReduceBuiltin() {
    super("/");
    valid = 0x010;
  }

  public Obj call(Obj f, Value w) {
    Value[] a = w.arr;
    if (a.length == 0) return ((Fun)f).identity;
    Value last = a[a.length-1];
    for (int i = a.length-2; i >= 0; i--) {
      last = (Value)((Fun)f).call(a[i], last);
    }
    return last;
  }
}