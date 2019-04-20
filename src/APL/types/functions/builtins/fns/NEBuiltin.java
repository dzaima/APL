package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.BitArr;
import APL.types.functions.Builtin;


public class NEBuiltin extends Builtin {
  @Override public String repr() {
    return "≠";
  }
  
  
  
  static class DNf extends D_NNeB {
    public boolean on(double a, double w) {
      return a != w;
    }
    public void on(BitArr.BC res, double a, double[] w) {
      for (double cw : w) res.add(a != cw);
    }
    public void on(BitArr.BC res, double[] a, double w) {
      for (double ca : a) res.add(ca != w);
    }
    public void on(BitArr.BC res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res.add(a[i] != w[i]);
    }
  }
  static class DBf implements D_BB {
    @Override public Value call(boolean a, BitArr w) {
      if (a) return TildeBuiltin.call(w);
      return w;
    }
    @Override public Value call(BitArr a, boolean w) {
      if (w) return TildeBuiltin.call(a);
      return a;
    }
    @Override public Value call(BitArr a, BitArr w) {
      BitArr.BC bc = BitArr.create(w.shape);
      for (int i = 0; i < bc.arr.length; i++) bc.arr[i] = a.arr[i] ^ w.arr[i];
      return bc.finish();
    }
  }
  private static final DNf DNF = new DNf();
  private static final DBf DBF = new DBf();
  
  public Obj call(Value a, Value w) {
    return ncbaD(DNF, DBF, (ca, cw) -> ca!=cw? Num.ONE : Num.ZERO, (ca, cw) -> ca.equals(cw)? Num.ONE : Num.ZERO, a, w);
  }
}
