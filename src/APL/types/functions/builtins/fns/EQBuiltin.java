package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.BitArr;
import APL.types.functions.Builtin;


public class EQBuiltin extends Builtin {
  @Override public String repr() {
    return "=";
  }
  
  
  
  private static final D_NNeB DNF = new D_NNeB() {
    public boolean on(double a, double w) {
      return a == w;
    }
    public void on(BitArr.BA res, double a, double[] w) {
      for (double cw : w) res.add(a == cw);
    }
    public void on(BitArr.BA res, double[] a, double w) {
      for (double ca : a) res.add(ca == w);
    }
    public void on(BitArr.BA res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res.add(a[i] == w[i]);
    }
    public Value call(BigValue a, BigValue w) {
      return a.equals(w)? Num.ONE : Num.ZERO;
    }
  };
  private static final D_BB DBF = new D_BB() {
    @Override public Value call(boolean a, BitArr w) {
      if (a) return w;
      return TildeBuiltin.call(w);
    }
    @Override public Value call(BitArr a, boolean w) {
      if (w) return a;
      return TildeBuiltin.call(a);
    }
    @Override public Value call(BitArr a, BitArr w) {
      BitArr.BC bc = BitArr.create(w.shape);
      for (int i = 0; i < bc.arr.length; i++) bc.arr[i] = ~(a.arr[i] ^ w.arr[i]);
      return bc.finish();
    }
  };
  
  public Value call(Value a, Value w) {
    return ncbaD(DNF, DBF, (ca, cw) -> ca==cw? Num.ONE : Num.ZERO, (ca, cw) -> ca.equals(cw)? Num.ONE : Num.ZERO, a, w);
  }
}
