package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.BitArr;
import APL.types.functions.Builtin;

import java.util.Arrays;


public class LTBuiltin extends Builtin {
  @Override public String repr() {
    return "<";
  }
  
  
  
  static class DNf extends D_NNeB {
    public boolean on(double a, double w) {
      return a < w;
    }
    public void on(BitArr.BC res, double a, double[] w) {
      for (double cw : w) res.add(a < cw);
    }
    public void on(BitArr.BC res, double[] a, double w) {
      for (double ca : a) res.add(ca < w);
    }
    public void on(BitArr.BC res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res.add(a[i] < w[i]);
    }
  }
  private static final DNf DNF = new DNf();
  
  public Obj call(Value a, Value w) {
    return numChrD(DNF, (ca, cw) -> ca<cw? Num.ONE : Num.ZERO,
      (ca, cw) -> { throw new DomainError("comparing "+ ca.humanType(true)+" and "+cw.humanType(true)); },
      a, w);
  }
  
  public Obj call(Value w) {
    var order = w.gradeUp();
    Value[] res = new Value[order.length];
    Arrays.setAll(res, i -> w.get(order[i]));
    return Arr.create(res);
  }
}
