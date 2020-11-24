package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.math.BigInteger;
import java.util.*;

public class DTackBuiltin extends Builtin {
  @Override public String repr() {
    return "⊤";
  }
  
  
  
  public Value call(Value w) {
    return call(Num.NUMS[2], w);
  }
  
  public Value callInv(Value w) {
    return UTackBuiltin.on(Num.NUMS[2], w, this);
  }
  public Value callInvW(Value a, Value w) {
    return UTackBuiltin.on(a, w, this);
  }
  
  public Value call(Value a, Value w) {
    return on(a, w, this);
  }
  
  public static Value on(Value a, Value w, Callable blame) {
    if (!(a instanceof Primitive)) {
      if (w instanceof BigValue) {
        ArrayList<Value> res = new ArrayList<>();
        BigInteger c = ((BigValue) w).i;
        for (int i = 0; i < a.ia; i++) {
          Value v = a.get(a.ia-i-1);
          BigInteger[] dr = c.divideAndRemainder(BigValue.bigint(v));
          res.add(v instanceof Num? new Num(dr[1].intValue()) : new BigValue(dr[1]));
          c = dr[0];
        }
        Collections.reverse(res);
        return Arr.create(res);
      }
      int[] sh = new int[w.rank+a.rank];
      if (a.rank != 1) throw new NYIError(blame+": ⍺ with rank≥2 not yet implemented", blame);
      
      System.arraycopy(a.shape, 0, sh, 0, a.rank); // ≡ for (int i = 0; i < a.rank; i++) sh[i] = a.shape[i];
      System.arraycopy(w.shape, 0, sh, a.rank, w.rank); // ≡ for (int i = 0; i < w.rank; i++) sh[i+a.rank] = w.shape[i];
      if (a.ia == 0) return new EmptyArr(sh, Num.ZERO);
      double[] c = w.asDoubleArrClone();
      double[] b = a.asDoubleArr();
      double[] res = new double[w.ia * a.ia];
      for (int i = 1; i < b.length; i++) if (b[i] == 0) throw new DomainError(blame+": ⍺ contained a 0 as not the 1st element", blame, a);
      int last = b[0] == 0? 1 : 0;
      for (int i = b.length-1; i >= last; i--) {
        int off = w.ia*i;
        double cb = b[i];
        for (int j = 0; j < w.ia; j++) {
          res[off + j] = c[j] % cb;
          c[j] = Math.floor(c[j] / cb);
        }
      }
      if (b[0] == 0) {
        System.arraycopy(c, 0, res, 0, w.ia); // ≡ for (int j = 0; j < w.ia; j++) res[j] = c[j];
      }
      return new DoubleArr(res, sh);
    }
    if (!(w instanceof Num)) {
      if (w instanceof BigValue) {
        BigInteger base = BigValue.bigint(a);
        boolean bigBase = a instanceof BigValue;
        BigInteger wlr = ((BigValue) w).i;
        int sign = wlr.signum();
        BigInteger wl = wlr.abs();
        int ibase = BigValue.safeInt(base);
        if (ibase <= 1) {
          if (ibase==1 && sign!=0) throw new DomainError(blame+": ⍺=1 and ⍵≠0 isn't possible", blame, w);
          if (ibase < 0) throw new DomainError(blame+": ⍺ < 0", blame);
        }
        if (sign==0) return EmptyArr.SHAPE0N;
        if (ibase == 2) {
          int len = wl.bitLength();
          if (bigBase) {
            Value[] res = new Value[len];
            if (sign==1) for (int i = 0; i < len; i++) res[len-i-1] = wl.testBit(i)? BigValue.      ONE : BigValue.ZERO;
            else         for (int i = 0; i < len; i++) res[len-i-1] = wl.testBit(i)? BigValue.MINUS_ONE : BigValue.ZERO;
            return new HArr(res);
          } else if (sign == 1) {
            BitArr.BA bc = new BitArr.BA(len);
            for (int i = 0; i < len; i++) bc.add(wl.testBit(len-i-1));
            return bc.finish();
          } else {
            double[] res = new double[len];
            for (int i = 0; i < len; i++) res[i] = wl.testBit(len-i-1)? -1 : 0;
            return new DoubleArr(res);
          }
        }
        if (ibase <= Character.MAX_RADIX) { // utilize the actually optimized base conversion of BigInteger.toString
          String str = wl.toString(ibase);
          Value[] res = new Value[str.length()];
          for (int i = 0; i < res.length; i++) {
            char c = str.charAt(i);
            int n = c<='9'? c-'0' : 10+c-'a';
            if (sign==-1) n=-n;
            res[i] = bigBase? new BigValue(BigInteger.valueOf(n)) : Num.of(n);
          }
          return new HArr(res);
        }
        ArrayList<Value> ns = new ArrayList<>(); // if we can't, just be lazy. ¯\_(ツ)_/¯
        while (wl.signum() != 0) {
          BigInteger[] c = wl.divideAndRemainder(base);
          wl = c[0];
          ns.add(bigBase? new BigValue(sign==1? c[1] : c[1].negate()) : new Num(c[1].intValue()*sign));
        }
        Value[] res = new Value[ns.size()];
        for (int i = 0; i < res.length; i++) {
          res[res.length-i-1] = ns.get(i);
        }
        return new HArr(res);
      }
      throw new NYIError(blame+": scalar ⍺ and non-scalar ⍵ not implemented", blame);
    }
    double base = a.asDouble();
    double num = w.asDouble();
    if (base <= 1) {
      if (base == 0) return Num.of(num);
      if (base < 0) throw new DomainError(blame+": ⍺ < 0", blame, a);
      throw new DomainError(blame+": ⍺ < 1", blame, a);
    }
    var res = new ArrayList<Double>();
    if (num < 0) {
      num = -num;
      while (num > 0) {
        res.add(-num%base);
        num = Math.floor(num/base);
      }
    } else {
      while (num > 0) {
        res.add(num%base);
        num = Math.floor(num/base);
      }
    }
    double[] f = new double[res.size()];
    for (int i = res.size()-1, j = 0; i >= 0; i--, j++) {
      f[j] = res.get(i);
    }
    return new DoubleArr(f);
  }
}