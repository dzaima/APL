package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class LogBuiltin extends Builtin {
  @Override public String repr() {
    return "⍟";
  }
  
  
  
  public static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return w.log(Num.E);
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.log(a[i]);
    }
  };
  public Obj call(Value w) {
    return numM(NF, w);
  }
  public Obj callInv(Value w) {
    return numM(StarBuiltin.NF, w);
  }
  
  public static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return Math.log(w) / Math.log(a);
    }
    public void on(double[] res, double a, double[] w) {
      double la = Math.log(a);
      for (int i = 0; i < w.length; i++) res[i] = Math.log(w[i]) / la;
    }
    public void on(double[] res, double[] a, double w) {
      double lw = Math.log(w);
      for (int i = 0; i < a.length; i++) res[i] = lw / Math.log(a[i]);
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.log(w[i]) / Math.log(a[i]);
    }
  };
  public Obj call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
  
  @Override public Obj callInvW(Value a, Value w) {
    return numD(StarBuiltin.DNF, a, w);
  }
  @Override public Obj callInvA(Value a, Value w) {
    return numD(RootBuiltin.DNF, a, w);
  }
}