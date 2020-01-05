package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.math.BigInteger;
import java.util.*;

public class DTackBuiltin extends Builtin {
  static final DTackBuiltin copy = new DTackBuiltin();
  @Override public String repr() {
    return "⊤";
  }
  
  
  
  public Obj call(Value w) {
    return call(Num.NUMS[2], w);
  }
  
  public Obj callInv(Value w) {
    return UTackBuiltin.copy.call(w);
  }
  public Obj callInvW(Value a, Value w) {
    return UTackBuiltin.copy.call(a, w);
  }
  
  public Obj call(Value a, Value w) {
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
        return HArr.create(res.toArray(new Value[0]));
      }
      int[] sh = new int[w.rank+a.rank];
      if (a.rank != 1) throw new NYIError("⍺ of ⊤ with rank≥2 not yet implemented", this);
//      for (int i = 0; i < a.rank; i++) sh[i] = a.shape[i];
//      for (int i = 0; i < w.rank; i++) sh[i+a.rank] = w.shape[i];
      System.arraycopy(a.shape, 0, sh, 0, a.rank); // yes yes this only works for a.rank==1
      System.arraycopy(w.shape, 0, sh, a.rank, w.rank);
      if (a.ia == 0) return new EmptyArr(sh);
      double[] c = w.asDoubleArrClone();
      double[] b = a.asDoubleArr();
      double[] res = new double[w.ia * a.ia];
      for (int i = 1; i < b.length; i++) if (b[i] == 0) throw new DomainError("base for ⊤ contained a 0 as not the 1st element", this, a);
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
//        for (int j = 0; j < w.ia; j++) res[j] = c[j];
        System.arraycopy(c, 0, res, 0, w.ia);
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
          if (ibase==1 && sign!=0) throw new DomainError("⍺=1 and ⍵≠0 isn't possible", this, w);
          if (ibase < 0) throw new DomainError("⊤: ⍺ < 0", this);
        }
        if (sign==0) return EmptyArr.SHAPE0;
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
      throw new NYIError("non-scalar number not implemented", this);
    }
    double base = a.asDouble();
    double num = w.asDouble();
    if (base <= 1) {
      if (base == 1 && num > 0) throw new DomainError("⍺=1 and ⍵>0 isn't possible", this, w);
      if (base < 0) throw new DomainError("⊤: ⍺ < 0", this);
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