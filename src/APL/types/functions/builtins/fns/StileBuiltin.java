package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class StileBuiltin extends Builtin {
  public StileBuiltin() {
    super("|", 0x011);
  }
  
  public Obj call(Value w) {
    return numChrMap(Num::abs, c->{ throw new DomainError("|char", w); }, c -> new Num(c.size()), w);
  }
  
  static class DNf implements DyNumVecFun {
    public double call(double a, double w) {
      double c = w % a;
      if (c < 0) return c + a;
      return c;
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) {
        double c = w[i] % a;
        if (c < 0) res[i] = c + a;
        else res[i] = c;
      }
    }
    public void call(double[] res, double[] a, double w) {
      if (w > 0) for (int i = 0; i < a.length; i++) res[i] = w % a[i];
      else       for (int i = 0; i < a.length; i++) {
        double c = w % a[i];
        if (c < 0) res[i] = c + a[i];
        else res[i] = c;
      }
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) {
        double c = w[i] % a[i];
        if (c < 0) res[i] = c + a[i];
        else res[i] = c;
      }
    }
  }
  private static DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
}