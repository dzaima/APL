package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.functions.Mop;

public class TableBuiltin extends Mop {
  public TableBuiltin() {
    super("⌾", 0x010);
  }
  public Obj call(Obj f, Value a, Value w) {
    Value[] arr = new Value[a.ia*w.ia];
    int[] shape = new int[a.rank+w.rank];
    System.arraycopy(a.shape, 0, shape, 0, a.rank);
    System.arraycopy(w.shape, 0, shape, a.rank, w.rank);
    int i = 0;
    for (Value na : a.arr) {
      for (Value nw : w.arr) {
        arr[i] = (Value)((Fun)f).call(na, nw);
        i++;
      }
    }
    if (shape.length == 0) return arr[0];
    return new Arr(arr, shape);
  }
}