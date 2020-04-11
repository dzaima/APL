package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class LogBuiltin extends Builtin {
  @Override public String repr() {
    return "⍟";
  }
  
  
  static final double LN2 = Math.log(2);
  
  public static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return w.log(Num.E);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.log(a[i]);
    }
    public Num call(BigValue w) {
      if (w.i.signum() <= 0) {
        if (w.i.signum() == -1) throw new DomainError("logarithm of negative number", w);
        return Num.NEGINF;
      }
      if (w.i.bitLength()<1023) return new Num(Math.log(w.i.doubleValue())); // safe quick path
      int len = w.i.bitLength();
      int shift = len > 64? len - 64 : 0; // 64 msb should be enough to get most out of log
      double d = w.i.shiftRight(shift).doubleValue();
      return new Num(Math.log(d) + LN2*shift);
    }
  };
  public Value call(Value w) {
    return numM(NF, w);
  }
  public Value callInv(Value w) {
    return numM(StarBuiltin.NF, w);
  }
  
  public static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return Math.log(w) / Math.log(a);
    }
    public void on(double[] res, double a, double[] w) {
      double la = Math.log(a);
      for (int i = 0; i < w.length; i++) res[i] = Math.log(w[i]) / la;
    }
    public void on(double[] res, double[] a, double w) {
      double lw = Math.log(w);
      for (int i = 0; i < a.length; i++) res[i] = lw / Math.log(a[i]);
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.log(w[i]) / Math.log(a[i]);
    }
    public Value call(double a, BigValue w) {
      double res = ((Num) NF.call(w)).num/Math.log(a);
      if (a==2) { // quick path to make sure 2⍟ makes sense
        int expected = w.i.bitLength()-1;
        // System.out.println(res+" > "+expected);
        if (res < expected)   return Num.of(expected);
        if (res >=expected+1) { // have to get the double juuuust below expected
          long repr = Double.doubleToRawLongBits(expected+1);
          repr--; // should be safe as positive int values are always well into the proper double domain
          return new Num(Double.longBitsToDouble(repr));
        }
      }
      return new Num(res);
    }
  };
  public Value call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
  
  @Override public Value callInvW(Value a, Value w) {
    return numD(StarBuiltin.DNF, a, w);
  }
  @Override public Value callInvA(Value a, Value w) {
    return numD(RootBuiltin.DNF, a, w);
  }
}