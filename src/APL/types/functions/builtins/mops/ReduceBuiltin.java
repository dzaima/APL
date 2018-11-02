package APL.types.functions.builtins.mops;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.dimensions.DimMMop;
import APL.types.functions.Mop;

public class ReduceBuiltin extends Mop implements DimMMop {
  public ReduceBuiltin() {
    super("/", 0x010);
  }
  
  @Override
  public Obj call(Obj f, Value w, int dim) {
    return ngnReduce(w, dim, (Fun)f);
  }
  
  public Obj call(Obj f, Value w) {
    // TODO ranks
    if (w.rank >= 2) {
      return ngnReduce(w, -1, (Fun) f);
    }
    Value[] a = w.values();
    if (a.length == 0) {
      if (((Fun)f).identity == null) throw new DomainError("No identity defined for "+f.name(), f);
      return ((Fun)f).identity;
    }
    Value last = a[a.length-1];
    for (int i = a.length-2; i >= 0; i--) {
      last = (Value)((Fun)f).call(a[i], last);
    }
    return last.squeeze();
  }
  private Value ngnReduce(Value x, int axis, Fun f) { // https://chat.stackexchange.com/transcript/message/47158587#47158587
    if (x.rank == 0) return x;
    if (axis < 0) axis+= x.rank;
    int n0 = 1; // product of all dimensions before "axis"
    for (int i = 0; i < axis; i++) {
      n0*= x.shape[i];
    }
    int n1 = x.shape[axis]; // the dimension at "axis" - what's getting removed/reduced
    int n2 = x.ia / (n1*n0); // product of the rest of the shape
    int[] ns = x.eraseDim(axis);
    
    Value[] r = new Value[n0 * n2];
    for (int i = 0; i < n0; i++) {
      for (int k = 0; k < n2; k++) {
        Value c = x.get(i*n1*n2 + (n1-1)*n2 + k);
        for (int j = n1 - 2; j >= 0; j--) {
          c = (Value) f.call(x.get(i*n1*n2 + j*n2 + k), c);
        }
        r[i*n2 + k] = c.squeeze();
      }
    }
    return Arr.create(r, ns);
  }
}