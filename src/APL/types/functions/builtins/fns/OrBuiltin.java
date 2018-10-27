package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.stream.IntStream;

public class OrBuiltin extends Builtin {
  public OrBuiltin() {
    super("∨", 0x011);
    identity = Num.ZERO;
  }
  
  public Obj call(Value w) {
    return new Num(Num.gcd(w.asDoubleArr()));
  }
  
  static class DNf implements DyNumVecFun {
    public double call(double a, double w) {
      return Num.gcd(a, w);
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Num.gcd(a, w[i]);
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.gcd(a[i], w);
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.gcd(a[i], w[i]);
    }
  }
  private static DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
}