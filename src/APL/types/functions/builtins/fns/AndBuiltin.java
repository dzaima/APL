package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class AndBuiltin extends Builtin {
  @Override public String repr() {
    return "∧";
  }
  
  
  
  public Value identity() {
    return Num.ONE;
  }
  
  public Obj call(Value w) {
    return new Num(Num.lcm(w.asDoubleArr()));
  }
  
  static class DNf implements NumDV {
    public double call(double a, double w) {
      return Num.lcm(a, w);
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Num.lcm(a, w[i]);
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.lcm(a[i], w);
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.lcm(a[i], w[i]);
    }
  }
  private static final DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
}