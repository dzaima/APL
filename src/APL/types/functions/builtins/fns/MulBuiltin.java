package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

public class MulBuiltin extends Builtin {
  public MulBuiltin() {
    super("×", 0x011);
  }
  
  static class Nf implements NumVecFun {
    public Value call(Num w) {
      return new Num(w.compareTo(Num.ZERO));
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = a[i]>0? 1 : a[i]<0? -1 : 0;
    }
  }
  private static Nf NF = new Nf();
  public Obj call(Value w) {
    return numChrMap(NF, c -> new Num(c.getCase()), c -> c.size()>0? Num.ONE : Num.ZERO, w);
  }
  
  static class DNf implements DyNumVecFun {
    public double call(double a, double w) {
      return a*w;
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a * w[i];
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w;
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w[i];
    }
  }
  private static DNf DNF = new DNf();
  public Obj call(Value a, Value w) {
    return scalarNum(DNF, a, w);
  }
  
  public Obj callInvW(Value a, Value w) {
    try {
      return new DivBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError("", e.cause);
    }
  }
}