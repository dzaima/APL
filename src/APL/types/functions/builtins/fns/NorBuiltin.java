package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class NorBuiltin extends Builtin {
  @Override public String repr() {
    return "⍱";
  }
  
  public NorBuiltin(Scope sc) {
    super(sc);
  }
  
  private static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return Main.bool(a)|Main.bool(w) ? 0 : 1;
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = Main.bool(a)|Main.bool(w[i]) ? 0 : 1;
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = Main.bool(a[i])|Main.bool(w) ? 0 : 1;
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = Main.bool(a[i])|Main.bool(w[i]) ? 0 : 1;
    }
  };
  
  private static final D_BB DBF = new D_BB() {
    @Override public Value call(boolean a, BitArr w) {
      if (a) return BitArr.fill(w, false);
      return TildeBuiltin.call(w);
    }
    @Override public Value call(BitArr a, boolean w) {
      if (w) return BitArr.fill(a, false);
      return TildeBuiltin.call(a);
    }
    @Override public Value call(BitArr a, BitArr w) {
      BitArr.BC bc = new BitArr.BC(a.shape);
      for (int i = 0; i < a.arr.length; i++) bc.arr[i] = ~(a.arr[i] | w.arr[i]);
      return bc.finish();
    }
  };
  
  public Obj call(Value a, Value w) {
    return bitD(DNF, DBF, a, w);
  }
  public Obj call(Value w) {
    if (w instanceof BitArr) {
      BitArr wb = (BitArr) w;
      wb.setEnd(false);
      for (long l : wb.arr) if (l != 0L) return Num.ZERO;
      return Num.ONE;
    }
    if (w instanceof DoubleArr) {
      double[] da = w.asDoubleArr();
      for (int i = 0; i < w.ia; i++) {
        if (Main.bool(da[i])) return Num.ZERO;
      }
      return Num.ONE;
    }
    for (int i = 0; i < w.ia; i++) {
      if (Main.bool(w.get(i))) return Num.ZERO;
    }
    return Num.ONE;
  }
}