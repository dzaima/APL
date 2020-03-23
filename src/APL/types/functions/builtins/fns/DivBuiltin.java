package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class DivBuiltin extends Builtin {
  @Override public String repr() {
    return "÷";
  }
  
  
  
  private static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return Num.ONE.divide(w);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = 1/a[i];
    }
    public Value call(BigValue w) {
      throw new DomainError("reciprocal of biginteger", w);
    }
  };
  public Value call(Value w) {
    return numM(NF, w);
  }
  
  private static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return a / w;
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a / w[i];
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] / w;
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] / w[i];
    }
    public Value call(BigValue a, BigValue w) {
      return new BigValue(a.i.divide(w.i));
    }
  };
  public Value call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
  
  public Value callInv(Value w) { return call(w); }
  public Value callInvW(Value a, Value w) { return call(a, w); }
  
  @Override public Value callInvA(Value a, Value w) {
    return numD(MulBuiltin.DNF, a, w);
  }
}
