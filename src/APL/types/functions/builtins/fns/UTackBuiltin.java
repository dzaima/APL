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
  
  
  
  public Value call(Value w) {
    return on(w, Num.NUMS[2], this);
  }
  
  public Value callInv(Value w) {
    return DTackBuiltin.copy.call(w);
  }
  public Value callInvW(Value a, Value w) {
    return DTackBuiltin.copy.call(a, w);
  }
  
  public Value call(Value a, Value w) {
    return on(a, w, this);
  }
  
  public static Value on(Value a, Value w, Tokenable t) { // a - arr, w - base
    if (a.rank == 0) throw new DomainError("num⊥A is pointless", t);
    if (w instanceof BigValue || w.first() instanceof BigValue || a.first() instanceof BigValue) {
      if (w.rank == 0) {
        BigInteger al = BigValue.bigint(w);
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < a.ia; i++) {
          res = res.multiply(al).add(BigValue.bigint(a.get(i)));
        }
        return new BigValue(res);
      } else {
        if (a.rank != 1) throw new NYIError("1<≢⍴⍵ for ⊥");
        if (w.rank != 1) throw new DomainError("1<≢⍴⍺ for ⊥");
        if (w.ia != a.shape[0]) throw new DomainError("(≢⍺) ≠ ≢⍵ for ⊥", t);
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < w.ia; i++) {
          res = res.multiply(BigValue.bigint(w.get(i)));
          res = res.add(BigValue.bigint(a.get(i)));
        }
        return new BigValue(res);
      }
    }
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
      if (w.ia != a.shape[0]) throw new DomainError("(≢⍺) ≠ ⊃⍴⍵ for ⊥", t);
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