package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class FloorBuiltin extends Builtin {
  public FloorBuiltin() {
    super("⌊", 0x011);
  }
  
  public Value identity() {
    return Num.POSINF;
  }
  
  static class Nf implements NumMV {
    public Value call(Num w) {
      return w.floor();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.floor(a[i]);
    }
  }
  private static final Nf NF = new Nf();
  public Obj call(Value w) {
    return numChrM(NF, Char::lower, w);
  }
  static class DNf implements NumDV {
    public double call(double a, double w) {
      return Math.min(a, w);
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Math.min(a, w[i]);
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.min(a[i], w);
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.min(a[i], w[i]);
    }
  }
  private static final DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
}