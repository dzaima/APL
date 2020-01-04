package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.BitArr;
import APL.types.functions.Builtin;

public class OrBuiltin extends Builtin {
  @Override public String repr() {
    return "∨";
  }
  
  
  
  public Value identity() {
    return Num.ZERO;
  }
  
  public Obj call(Value w) {
    if (w instanceof BitArr) {
      BitArr wb = (BitArr) w;
      wb.setEnd(false);
      for (long l : wb.arr) if (l != 0L) return Num.ONE;
      return Num.ZERO;
    }
    return new Num(Num.gcd(w.asDoubleArr()));
  }
  
  private static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return Num.gcd(a, w);
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Num.gcd(a, w[i]);
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.gcd(a[i], w);
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Num.gcd(a[i], w[i]);
    }
    public Value call(BigValue a, BigValue w) {
      return new BigValue(a.i.gcd(w.i));
    }
  };
  
  private static final D_BB DBF = new D_BB() {
    @Override public Value call(boolean a, BitArr w) {
      if (a) return BitArr.fill(w, true);
      return w;
    }
    @Override public Value call(BitArr a, boolean w) {
      if (w) return BitArr.fill(a, true);
      return a;
    }
    @Override public Value call(BitArr a, BitArr w) {
      BitArr.BC bc = new BitArr.BC(a.shape);
      for (int i = 0; i < a.arr.length; i++) bc.arr[i] = a.arr[i] | w.arr[i];
      return bc.finish();
    }
  };
  public Obj call(Value a0, Value w0) {
    return bitD(DNF, DBF, a0, w0);
  }
}