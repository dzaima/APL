package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

public class UTackBuiltin extends Builtin {
  static final UTackBuiltin copy = new UTackBuiltin();
  @Override public String repr() {
    return "⊥";
  }
  
  
  
  public Obj call(Value w) {
    return call(w, Num.TWO);
  }
  
  public Obj callInv(Value w) {
    return DTackBuiltin.copy.call(w);
  }
  public Obj callInvW(Value a, Value w) {
    return DTackBuiltin.copy.call(a, w);
  }
  
  public Obj call(Value a, Value w) {
    if (a.rank == 0) throw new DomainError("A⊥num is pointless", this);
    if (w instanceof Num) {
      double base = w.asDouble();
      if (a.rank == 1) {
        double res = 0;
        for (int i = 0; i < a.ia; i++) {
          res = res*base + a.get(i).asDouble();
        }
        return new Num(res);
      } else {
        double[] d = a.asDoubleArr();
        int[] sh = new int[a.rank-1];
        System.arraycopy(a.shape, 1, sh, 0, a.rank - 1);
        int layers = a.shape[0];
        double[] r = new double[a.ia / layers];
  
        System.arraycopy(d, 0, r, 0, r.length);
        for (int i = 1; i < layers; i++) {
          for (int j = 0; j < r.length; j++) {
            r[j] = r[j]*base + d[j+r.length*i];
          }
        }
        
        return new DoubleArr(r, sh);
      }
    } else {
      if (w.ia != a.shape[0]) throw new DomainError("(≢⍺) ≠ ⊃⍴⍵ for ⊥", this);
      double[] d = a.asDoubleArr();
      double[] bases = w.asDoubleArr();
      int[] sh = new int[a.rank-1];
      System.arraycopy(a.shape, 1, sh, 0, a.rank - 1);
      int layers = a.shape[0];
      double[] r = new double[a.ia /layers];
  
      System.arraycopy(d, 0, r, 0, r.length);
      for (int i = 1; i < layers; i++) {
        double base = bases[i];
        for (int j = 0; j < r.length; j++) {
          r[j] = r[j]*base + d[j+r.length*i];
        }
      }
      if (sh.length == 0) return new Num(r[0]);
      return new DoubleArr(r, sh);
    }
  }
}