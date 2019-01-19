package APL.types.functions.builtins.mops;

import APL.errors.*;
import APL.types.*;
import APL.types.dimensions.DimMMop;
import APL.types.functions.Mop;
import APL.types.functions.builtins.fns.*;

public class ReduceBuiltin extends Mop implements DimMMop {
  @Override public String repr() {
    return "/";
  }
  
  
  
  @Override
  public Obj call(Obj f, Value w, int dim) {
    return ngnReduce(w, dim, (Fun)f);
  }
  
  public Obj call(Obj f, Value w) {
    Fun ff = (Fun) f;
    if (w.rank >= 2) {
      return ngnReduce(w, -1, ff);
    }
    if (f instanceof PlusBuiltin && w.quickDoubleArr()) {
      double s = 0;
      for (double d : w.asDoubleArr()) s+= d;
      return new Num(s);
    }
    if (f instanceof MulBuiltin && w.quickDoubleArr()) {
      double p = 0;
      for (double d : w.asDoubleArr()) p*= d;
      return new Num(p);
    }
    if (f instanceof FloorBuiltin && w.quickDoubleArr()) {
      double p = Double.MAX_VALUE;
      for (double d : w.asDoubleArr()) p = Math.min(p, d);
      return new Num(p);
    }
    if (f instanceof CeilingBuiltin && w.quickDoubleArr()) {
      double p = Double.MIN_VALUE;
      for (double d : w.asDoubleArr()) p = Math.max(p, d);
      return new Num(p);
    }
    Value[] a = w.values();
    if (a.length == 0) {
      Value id = ff.identity();
      if (id == null) throw new DomainError("No identity defined for "+f.name(), f);
      return id;
    }
    Value last = a[a.length-1];
    for (int i = a.length-2; i >= 0; i--) {
      last = (Value) ff.call(a[i], last);
    }
    return last.squeeze();
  }
  
  public Obj call(Obj f, Value a, Value w) {
    if (w.rank != 1) throw new NYIError("A f/ B with 2≤⍴⍴B hasn't been implemented", w);
    if (!(f instanceof Fun)) throw new DomainError("operand to / must be a function");
    int n = a.asInt();
    Value[] ra = new Value[w.ia - Math.abs(n) + 1];
    Value[] wa = w.values();
    if (n > 0) {
      for (int i = 0; i < ra.length; i++) {
        Value r = wa[i+n-1];
        for (int j = n-2; j >= 0; j--) {
          r = (Value) ((Fun) f).call(wa[i + j], r);
        }
        ra[i] = r;
      }
    } else {
      n = -n;
      for (int i = 0; i < ra.length; i++) {
        Value r = wa[i];
        for (int j = 1; j < n; j++) {
          r = (Value) ((Fun) f).call(wa[i + j], r);
        }
        ra[i] = r;
      }
    }
    return Arr.create(ra);
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