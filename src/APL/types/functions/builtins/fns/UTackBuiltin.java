package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

import java.math.BigInteger;

public class UTackBuiltin extends Builtin {
  static final UTackBuiltin copy = new UTackBuiltin();
  @Override public String repr() {
    return "⊥";
  }
  
  
  
  public Obj call(Value w) {
    return call(Num.NUMS[2], w);
  }
  
  public Obj callInv(Value w) {
    return DTackBuiltin.copy.call(w);
  }
  public Obj callInvW(Value a, Value w) {
    return DTackBuiltin.copy.call(a, w);
  }
  
  public Obj call(Value a, Value w) {
    if (w.rank == 0) throw new DomainError("A⊥num is pointless", this);
    if (a instanceof BigValue || a.first() instanceof BigValue || w.first() instanceof BigValue) {
      if (a.rank == 0) {
        BigInteger al = BigValue.bigint(a);
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < w.ia; i++) {
          res = res.multiply(al).add(BigValue.bigint(w.get(i)));
        }
        return new BigValue(res);
      } else {
        if (w.rank != 1) throw new NYIError("1<≢⍴⍵ for ⊥");
        if (a.rank != 1) throw new DomainError("1<≢⍴⍺ for ⊥");
        if (a.ia != w.shape[0]) throw new DomainError("(≢⍺) ≠ ≢⍵ for ⊥", this);
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < a.ia; i++) {
          res = res.multiply(BigValue.bigint(a.get(i)));
          res = res.add(BigValue.bigint(w.get(i)));
        }
        return new BigValue(res);
      }
    }
    if (a instanceof Num) {
      double base = a.asDouble();
      if (w.rank == 1) {
        double res = 0;
        for (int i = 0; i < w.ia; i++) {
          res = res*base + w.get(i).asDouble();
        }
        return new Num(res);
      } else {
        double[] d = w.asDoubleArr();
        int[] sh = new int[w.rank-1];
        System.arraycopy(w.shape, 1, sh, 0, w.rank - 1);
        int layers = w.shape[0];
        double[] r = new double[w.ia / layers];
  
        System.arraycopy(d, 0, r, 0, r.length);
        for (int i = 1; i < layers; i++) {
          for (int j = 0; j < r.length; j++) {
            r[j] = r[j]*base + d[j+r.length*i];
          }
        }
        
        return new DoubleArr(r, sh);
      }
    } else {
      if (a.ia != w.shape[0]) throw new DomainError("(≢⍺) ≠ ⊃⍴⍵ for ⊥", this);
      double[] d = w.asDoubleArr();
      double[] bases = a.asDoubleArr();
      int[] sh = new int[w.rank-1];
      System.arraycopy(w.shape, 1, sh, 0, w.rank - 1);
      int layers = w.shape[0];
      double[] r = new double[w.ia /layers];
  
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