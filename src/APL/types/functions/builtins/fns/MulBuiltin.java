package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class MulBuiltin extends Builtin {
  @Override public String repr() {
    return "×";
  }
  
  
  
  public Value identity() {
    return Num.ONE;
  }
  
  private static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      double d = w.num;
      return d>0? Num.ONE : d<0? Num.MINUS_ONE : Num.ZERO;
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = a[i]>0? 1 : a[i]<0? -1 : 0;
    }
    public Value call(BigValue w) {
      return Num.of(w.i.signum());
    }
  };
  public Value call(Value w) {
    return numChrMapM(NF, c -> Num.of(c.getCase()), c -> c.size()>0? Num.ONE : Num.ZERO, w);
  }
  
  static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return a*w;
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a * w[i];
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w;
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w[i];
    }
    public Value call(BigValue a, BigValue w) {
      return new BigValue(a.i.multiply(w.i));
    }
  };
  public Value call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  
  public Value callInvW(Value a, Value w) {
    try {
      return new DivBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError(e.getMessage(), this, e.cause);
    }
  }
  
  @Override public Value callInvA(Value a, Value w) {
    return callInvW(w, a);
  }
}