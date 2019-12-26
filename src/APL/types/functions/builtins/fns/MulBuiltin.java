package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class MulBuiltin extends Builtin {
  @Override public String repr() {
    return "×";
  }
  
  
  
  public Value identity() {
    return Num.ONE;
  }
  
  private static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return Num.of(w.compareTo(Num.ZERO));
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = a[i]>0? 1 : a[i]<0? -1 : 0;
    }
  };
  public Obj call(Value w) {
    return numChrMapM(NF, c -> Num.of(c.getCase()), c -> c.size()>0? Num.ONE : Num.ZERO, w);
  }
  
  static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return a*w;
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a * w[i];
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w;
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w[i];
    }
  };
  public Obj call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  
  public Obj callInvW(Value a, Value w) {
    try {
      return new DivBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError(e.getMessage(), this, e.cause);
    }
  }
  
  @Override public Obj callInvA(Value a, Value w) {
    return callInvW(w, a);
  }
}