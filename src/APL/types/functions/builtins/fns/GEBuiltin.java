package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;


public class GEBuiltin extends Builtin {
  @Override public String repr() {
    return "≥";
  }
  
  
  
  static class DNf implements NumDV {
    public double call(double a, double w) {
      return a >= w? 1 : 0;
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a >= w[i]? 1 : 0;
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] >= w? 1 : 0;
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] >= w[i]? 1 : 0;
    }
  }
  private static final DNf DNF = new DNf();
  
  public Obj call(Value a, Value w) {
    return numChrD(DNF, (ca, cw) -> ca>=cw? Num.ONE : Num.ZERO,
      (ca, cw) -> { throw new DomainError("comparing "+ ca.humanType(true)+" and "+cw.humanType(true)); },
      a, w);
  }
}
