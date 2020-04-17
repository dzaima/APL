package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

import java.math.BigInteger;

public class UTackBuiltin extends Builtin {
  @Override public String repr() {
    return "⊥";
  }
  
  
  
  public Value call(Value w) {
    return call(Num.NUMS[2], w);
  }
  
  public Value callInv(Value w) {
    return DTackBuiltin.on(Num.NUMS[2], w, this);
  }
  public Value callInvW(Value a, Value w) {
    return DTackBuiltin.on(a, w, this);
  }
  
  public Value call(Value a, Value w) {
    return on(a, w, this);
  }
  
  public static Value on(Value a, Value w, Callable blame) {
    if (w.rank == 0) throw new DomainError("A⊥num is pointless", blame);
    if (a instanceof BigValue || a.first() instanceof BigValue || w.first() instanceof BigValue) {
      if (a.rank == 0) {
        BigInteger al = BigValue.bigint(a);
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < w.ia; i++) {
          res = res.multiply(al).add(BigValue.bigint(w.get(i)));
        }
        return new BigValue(res);
      } else {
        if (w.rank != 1) throw new NYIError(blame+": 1<≢⍴⍵", blame);
        if (a.rank != 1) throw new DomainError(blame+": 1<≢⍴⍺", blame);
        if (a.ia != w.shape[0]) throw new DomainError(blame+": (≢⍺) ≠ ≢⍵", blame);
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
      if (a.ia != w.shape[0]) throw new DomainError(blame+": (≢⍺) ≠ ⊃⍴⍵", blame);
      double[] d = w.asDoubleArr();
      double[] bases = a.asDoubleArr();
      int[] sh = new int[w.rank-1];
      System.arraycopy(w.shape, 1, sh, 0, w.rank - 1);
      int layers = w.shape[0];
      double[] r = new double[w.ia/layers];
      
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