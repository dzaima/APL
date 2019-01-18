package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;


public class GTBuiltin extends Builtin {
  public GTBuiltin() {
    super(">", 0x010);
  }
  
  static class DNf implements NumDV {
    public double call(double a, double w) {
      return a > w? 1 : 0;
    }
    public void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a > w[i]? 1 : 0;
    }
    public void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] > w? 1 : 0;
    }
    public void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] > w[i]? 1 : 0;
    }
  }
  private static final DNf DNF = new DNf();
  
  public Obj call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  
  public Obj call(Value w) {
    var order = w.gradeDown();
    Value[] res = new Value[order.length];
    Arrays.setAll(res, i -> w.get(order[i]));
    return Arr.create(res);
  }
}
