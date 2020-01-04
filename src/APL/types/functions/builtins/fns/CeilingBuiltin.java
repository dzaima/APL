package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class CeilingBuiltin extends Builtin {
  @Override public String repr() {
    return "⌈";
  }
  
  
  
  public Value identity() {
    return Num.NEGINF;
  }
  
  private static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return w.ceil();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.ceil(a[i]);
    }
  };
  public Obj call(Value w) {
    return numChrM(NF, Char::upper, w);
  }
  
  private static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return Math.max(a, w);
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Math.max(a, w[i]);
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.max(a[i], w);
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Math.max(a[i], w[i]);
    }
    public Value call(BigValue a, BigValue w) {
      return a.compareTo(w)>0? a : w;
    }
  };
  public Obj call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
}