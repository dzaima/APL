package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class AndBuiltin extends Builtin {
  public AndBuiltin() {
    super("∧", 0x011);
    identity = Num.ONE;
  }
  
  public Obj call(Value w) {
    return new Num(Num.lcm(w.asDoubleArr()));
  }
  
  static class DNf implements DyNumVecFun {
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
    return scalarNum(DNF, a0, w0);
  }
}