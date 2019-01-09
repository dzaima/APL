package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;


public class PlusBuiltin extends Builtin {
  public PlusBuiltin() {
    super("+", 0x011);
  }

  public Obj call(Value w) {
    return scalar(v -> {
      if (!(v instanceof Num)) throw new DomainError("Conjugating a non-number", w); // TODO decide whether this should exist
      return ((Num)v).conjugate();
    }, w);
  }
  
  static class DNf implements DyNumVecFun {
    public double call(double a, double w) {
      return a + w;
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a + w[i];
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] + w;
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] + w[i];
    }
  }
  private static final DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) {
    try {
      return new MinusBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError("", e.cause);
    }
  }
}