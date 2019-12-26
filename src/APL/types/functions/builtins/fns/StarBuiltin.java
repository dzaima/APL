package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class StarBuiltin extends Builtin {
  @Override public String repr() {
    return "*";
  }
  
  
  
  static class Nf implements NumMV {
    public Value call(Num w) {
      return Num.E.pow(w);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.exp(a[i]);
    }
  }
  public static final Nf NF = new Nf();
  public Obj call(Value w) {
    return numM(NF, w);
  }
  public Obj callInv(Value w) {
    return numM(LogBuiltin.NF, w);
  }
  
  static class DNf extends D_NNeN {
    public double on(double a, double w) {
      return Math.pow(a, w);
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Math.pow(a, w[i]);
    }
    public void on(double[] res, double[] a, double w) {
      if (w == 2) for (int i = 0; i < a.length; i++) res[i] = a[i]*a[i];
      else for (int i = 0; i < a.length; i++) res[i] = Math.pow(a[i], w);
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.pow(a[i], w[i]);
    }
  }
  static final DNf DNF = new DNf();
  public Obj call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  public Obj callInvW(Value a, Value w) {
    return numD(LogBuiltin.DNF, a, w);
  }
  public Obj callInvA(Value a, Value w) {
    return numD(RootBuiltin.DNF, w, a);
  }
}