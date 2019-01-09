package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class StarBuiltin extends Builtin {
  public StarBuiltin() {
    super("*", 0x011);
  }
  
  static class Nf implements NumVecFun {
    public Value call(Num w) {
      return Num.E.pow(w);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.exp(a[i]);
    }
  }
  private static final Nf NF = new Nf();
  public Obj call(Value w) {
    return num(NF, w);
  }
  
  static class DNf implements DyNumVecFun {
    public double call(double a, double w) {
      return Math.pow(a, w);
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Math.pow(a, w[i]);
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.pow(a[i], w);
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.pow(a[i], w[i]);
    }
  }
  private static final DNf DNF = new DNf();
  public Obj call(Value a, Value w) {
    return scalarNum(DNF, a, w);
  }
}