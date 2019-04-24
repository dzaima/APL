package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class MinusBuiltin extends Builtin {
  @Override public String repr() {
    return "-";
  }
  
  
  
  static class Nf implements NumMV {
    public Value call(Num n) {
      return n.negate();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = -a[i];
    }
  }
  private static final Nf NF = new Nf();
  
  public Obj call(Value w) {
    return numChrM(NF, Char::swap, w);
  }
  
  static class DNf extends D_NNeN {
    public double on(double a, double w) {
      return a - w;
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a - w[i];
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] - w;
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] - w[i];
    }
  }
  private static final DNf DNF = new DNf();
  
  public Obj call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) { return call(a, w); }
}