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
      return w % a;
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = w[i] % a;
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = w % a[i];
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = w[i] % a[i];
    }
  }
  private static DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
}