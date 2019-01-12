package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class CeilingBuiltin extends Builtin {
  public CeilingBuiltin() {
    super("⌈", 0x011);
  }
  
  public Value identity() {
    return Num.NEGINF;
  }
  
  static class Nf implements NumVecFun {
    public Value call(Num w) {
      return w.ceil();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.ceil(a[i]);
    }
  }
  private static final Nf NF = new Nf();
  public Obj call(Value w) {
    return numChr(NF, Char::upper, w);
  }
  static class DNf implements DyNumVecFun {
    public double call(double a, double w) {
      return Math.max(a, w);
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Math.max(a, w[i]);
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.max(a[i], w);
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.max(a[i], w[i]);
    }
  }
  private static final DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
}