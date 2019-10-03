package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.ArrayList;

public class DTackBuiltin extends Builtin {
  static final DTackBuiltin copy = new DTackBuiltin();
  @Override public String repr() {
    return "⊤";
  }
  
  
  
  public Obj call(Value w) {
    return call(w, Num.TWO);
  }
  
  public Obj callInv(Value w) {
    return UTackBuiltin.copy.call(w);
  }
  public Obj callInvW(Value a, Value w) {
    return UTackBuiltin.copy.call(a, w);
  }
  
  public Obj call(Value a, Value w) {
    if (!(w instanceof Primitive)) {
      int[] sh = new int[a.rank+w.rank];
      if (w.rank != 1) throw new NYIError("⍺ of ⊤ with rank≥2 not yet implemented", this);
//      for (int i = 0; i < a.rank; i++) sh[i] = a.shape[i];
//      for (int i = 0; i < w.rank; i++) sh[i+a.rank] = w.shape[i];
      System.arraycopy(w.shape, 0, sh, 0, w.rank); // yes yes this only works for a.rank==1
      System.arraycopy(a.shape, 0, sh, w.rank, a.rank);
      if (w.ia == 0) return new EmptyArr(sh);
      double[] c = a.asDoubleArrClone();
      double[] b = w.asDoubleArr();
      double[] res = new double[a.ia * w.ia];
      for (int i = 1; i < b.length; i++) if (b[i] == 0) throw new DomainError("base for ⊤ contained a 0 as not the 1st element", this, w);
      int last = b[0] == 0? 1 : 0;
      for (int i = b.length-1; i >= last; i--) {
        int off = a.ia*i;
        double cb = b[i];
        for (int j = 0; j < a.ia; j++) {
          res[off + j] = c[j] % cb;
          c[j] = Math.floor(c[j] / cb);
        }
      }
      if (b[0] == 0) {
//        for (int j = 0; j < w.ia; j++) res[j] = c[j];
        System.arraycopy(c, 0, res, 0, a.ia);
      }
      return new DoubleArr(res, sh);
    }
    if (!(a instanceof Num)) throw new NYIError("non-scalar number not implemented", this);
    double base = w.asDouble();
    double num = a.asDouble();
    if (base == 1 && num > 0) throw new DomainError("⍺=1 and ⍵>0 isn't possible", this, a);
    var res = new ArrayList<Double>();
    while (num > 0) {
      res.add(num%base);
      num = Math.floor(num/base);
    }
    double[] f = new double[res.size()];
    for (int i = res.size()-1, j = 0; i >= 0; i--, j++) {
      f[j] = res.get(i);
    }
    return new DoubleArr(f);
  }
}