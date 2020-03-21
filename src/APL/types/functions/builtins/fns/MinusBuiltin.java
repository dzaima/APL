package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class MinusBuiltin extends Builtin {
  @Override public String repr() {
    return "-";
  }
  
  
  
  static final NumMV NF = new NumMV() {
    public Value call(Num n) {
      return n.negate();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = -a[i];
    }
    public Value call(BigValue w) {
      return new BigValue(w.i.negate());
    }
  };
  
  public Value call(Value w) {
    return numChrM(NF, Char::swap, w);
  }
  
  public static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return a - w;
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a - w[i];
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] - w;
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] - w[i];
    }
    public Value call(BigValue a, BigValue w) {
      return new BigValue(a.i.subtract(w.i));
    }
  };
  
  public Value call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  public Value callInv(Value w) { return call(w); }
  public Value callInvW(Value a, Value w) { return call(a, w); }
  public Value callInvA(Value a, Value w) {
    return numD(PlusBuiltin.DNF, a, w);
  }
}