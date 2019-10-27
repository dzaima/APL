package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.BitArr;
import APL.types.functions.Builtin;

public class AndBuiltin extends Builtin {
  @Override public String repr() {
    return "∧";
  }
  
  
  
  public Value identity() {
    return Num.ONE;
  }
  
  public Obj call(Value w) {
    if (w instanceof BitArr) {
      BitArr wb = (BitArr) w;
      wb.setEnd(true);
      for (long l : wb.arr) if (l != ~0L) return Num.ZERO;
      return Num.ONE;
    }
    return new Num(Num.lcm(w.asDoubleArr()));
  }
  
  static class DNf extends D_NNeN {
    public double on(double a, double w) {
      return Num.lcm(a, w);
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Num.lcm(a, w[i]);
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.lcm(a[i], w);
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.lcm(a[i], w[i]);
    }
  }
  private static final DNf DNF = new DNf();
  
  static class DBf implements D_BB {
    @Override public Value call(boolean a, BitArr w) {
      if (a) return w;
      return BitArr.fill(w, false);
    }
    @Override public Value call(BitArr a, boolean w) {
      if (w) return a;
      return BitArr.fill(a, false);
    }
    @Override public Value call(BitArr a, BitArr w) {
      BitArr.BC bc = new BitArr.BC(a.shape);
      for (int i = 0; i < a.arr.length; i++) bc.arr[i] = a.arr[i] & w.arr[i];
      return bc.finish();
    }
  }
  private static final DBf DBF = new DBf();
  
  public Obj call(Value a, Value w) {
    return bitD(DNF, DBF, a, w);
  }
}