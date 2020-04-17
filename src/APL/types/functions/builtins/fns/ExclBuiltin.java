package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

import java.math.BigInteger;

public class ExclBuiltin extends Builtin {
  @Override public String repr() {
    return "!";
  }
  
  
  private static final double[] cache = new double[172];
  static {
    double r = 1;
    cache[0] = cache[1] = r;
    for (int i = 2; i < 172; i++) {
      r*= i;
      cache[i] = r;
    }
  }
  
  private static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return new Num(cache[Math.min(w.asInt(), 171)]);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) {
        res[i] = cache[Math.min((int) a[i], 171)];
      }
    }
    public Value call(BigValue w) {
      if (w.i.bitLength() > 30) throw new DomainError("!: argument too big (⍵ ≡ "+w+")", w); // otherwise intValue might ignore those!
      int am = w.i.intValue();
      BigInteger res = BigInteger.ONE;
      for (int i = 2; i <= am; i++) {
        res = res.multiply(BigInteger.valueOf(i));
      }
      return new BigValue(res);
    }
  };
  
  public Value call(Value w) {
    return numM(NF, w);
  }
  
  public Value call(Value a0, Value w0) {
    return allD((a, w) -> {
      if (a instanceof BigValue || w instanceof BigValue) {
        
        BigInteger res = BigInteger.ONE;
        BigInteger al = BigValue.bigint(w);
        BigInteger bl = BigValue.bigint(a);
        if (al.compareTo(bl) < 0) return Num.ZERO;
  
        if (bl.compareTo(al.subtract(bl)) > 0) bl = al.subtract(bl);
        
        if (bl.bitLength() > 30) throw new DomainError("!: arguments too big (⍺ ≡ "+a+"; ⍵ ≡ "+w+")", w);
        int ri = bl.intValue();
        
        for (int i = 0; i < ri; i++) {
          res = res.multiply(al.subtract(BigInteger.valueOf(i)));
        }
        for (int i = 0; i < ri; i++) {
          res = res.divide(BigInteger.valueOf(i+1));
        }
        return new BigValue(res);
      }
      return ((Num) w).binomial((Num) a);
    }, a0, w0);
  }
}