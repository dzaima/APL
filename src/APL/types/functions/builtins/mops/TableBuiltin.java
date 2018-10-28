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
    Fun ff = (Fun) f;
    if (a == w) {
      Value[] vs = w.values();
      for (Value na : vs) {
        for (Value nw : vs) arr[i++] = ((Value) ff.call(na, nw)).squeeze();
      }
      if (shape.length == 0) return arr[0];
      return Arr.create(arr, shape);
    } else {
      for (Value na : a) {
        for (Value nw : w) {
          arr[i++] = ((Value) ff.call(na, nw)).squeeze();
        }
      }
      if (shape.length == 0) return arr[0];
      return Arr.create(arr, shape);
    }
  }
}