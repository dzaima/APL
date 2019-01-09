package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class MinusBuiltin extends Builtin {
  public MinusBuiltin() {
    super("-", 0x011);
  }
  
  static class Nf implements NumVecFun {
    public Value call(Num w) {
      return w.negate();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = -a[i];
    }
  }
  private static final Nf NF = new Nf();
  
  public Obj call(Value w) {
    return numChr(NF, Char::swap, w);
  }
  
  static class DNf implements DyNumVecFun {
    public double call(double a, double w) {
      return a - w;
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a - w[i];
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] - w;
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] - w[i];
    }
  }
  private static final DNf DNF = new DNf();
  
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) { return call(a, w); }
}