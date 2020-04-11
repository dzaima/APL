package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

import java.math.BigInteger;

public class StarBuiltin extends Builtin {
  @Override public String repr() {
    return "*";
  }
  
  
  
  public static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return Num.E.pow(w);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.exp(a[i]);
    }
  };
  public Value call(Value w) {
    return numM(NF, w);
  }
  public Value callInv(Value w) {
    return numM(LogBuiltin.NF, w);
  }
  
  static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return Math.pow(a, w);
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Math.pow(a, w[i]);
    }
    public void on(double[] res, double[] a, double w) {
      if (w == 2) for (int i = 0; i < a.length; i++) res[i] = a[i]*a[i];
      else for (int i = 0; i < a.length; i++) res[i] = Math.pow(a[i], w);
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.pow(a[i], w[i]);
    }
    public Value call(BigValue a, BigValue w) {
      if (a.i.signum() == 0) return BigValue.ZERO;
      if (a.i.equals(BigInteger.ONE)) return BigValue.ONE;
      if (a.i.equals(BigValue.MINUS_ONE.i)) return w.i.intValue()%2 == 0? BigValue.ONE : BigValue.MINUS_ONE;
      if (w.i.bitLength() > 30) throw new DomainError("right argument of * too big", w); // otherwise intValue might ignore those!
      return new BigValue(a.i.pow(w.i.intValue()));
    }
  };
  public Value call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  public Value callInvW(Value a, Value w) {
    return numD(LogBuiltin.DNF, a, w);
  }
  public Value callInvA(Value a, Value w) {
    return numD(RootBuiltin.DNF, w, a);
  }
}