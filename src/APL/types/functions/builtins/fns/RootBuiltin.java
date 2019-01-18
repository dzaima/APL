package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class RootBuiltin extends Builtin {
  public RootBuiltin() {
    super("√", 0x11);
  }
  
  static class Nf implements NumMV {
    public Value call(Num w) {
      return w.root(Num.TWO);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.sqrt(a[i]);
    }
  }
  private static final Nf NF = new Nf();
  public Obj call(Value w) {
    return numM(NF, w);
  }
  
  static class DNf implements NumDV {
    public double call(double a, double w) {
      return Math.pow(w, 1/a);
    }
    public void call(double[] res, double a, double[] w) {
      double pow = 1/a;
      for (int i = 0; i < w.length; i++) res[i] = Math.pow(w[i], pow);
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.pow(w, 1/a[i]);
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.pow(w[i], 1/a[i]);
    }
  }
  private static final DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
}