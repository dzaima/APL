package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class LogBuiltin extends Builtin {
  public LogBuiltin() {
    super("⍟", 0x011);
  }
  
  static class Nf implements NumMV {
    public Value call(Num w) {
      return w.log(Num.E);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.log(a[i]);
    }
  }
  private static final Nf NF = new Nf();
  public Obj call(Value w) {
    return numM(NF, w);
  }
  
  static class DNf implements NumDV {
    public double call(double a, double w) {
      return Math.log(w) / Math.log(a);
    }
    public void call(double[] res, double a, double[] w) {
      double la = Math.log(a);
      for (int i = 0; i < w.length; i++) res[i] = Math.log(w[i]) / la;
    }
    public void call(double[] res, double[] a, double w) {
      double lw = Math.log(w);
      for (int i = 0; i < a.length; i++) res[i] = lw / Math.log(a[i]);
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.log(w[i]) / Math.log(a[i]);
    }
  }
  private static final DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
}