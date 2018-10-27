package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

public class MinusBuiltin extends Builtin {
  public MinusBuiltin() {
    super("-", 0x011);
  }
  
  static class Nf implements NumVecFun {
    public Value call(Num w) {
      return w.negate();
    }
    public Arr call(DoubleArr a) {
      double[] n = new double[a.ia];
      for (int i = 0; i < a.ia; i++) n[i] = -a.arr[i];
      return new DoubleArr(n, a.shape);
    }
  }
  private static Nf NF = new Nf();
  
  public Obj call(Value w) {
    return numChr(NF, Char::swap, w);
  }
  
  static class DNf implements DyNumVecFun {
    public Value call(Num a, Num w) {
      return a.minus(w);
    }
    public Arr call(Num a, DoubleArr w) {
      double[] res = new double[w.ia]; double   av = a.num; double[] wv = w.arr;
      for (int i = 0; i < w.ia; i++) res[i] = av   -wv[i];
      return new DoubleArr(res, w.shape);
    }
    public Arr call(DoubleArr a, Num w) {
      double[] res = new double[a.ia]; double[] av = a.arr; double   wv = w.num;
      for (int i = 0; i < a.ia; i++) res[i] = av[i]-wv   ;
      return new DoubleArr(res, a.shape);
    }
    public Arr call(DoubleArr a, DoubleArr w) {
      double[] res = new double[a.ia]; double[] av = a.arr; double[] wv = w.arr;
      for (int i = 0; i < w.ia; i++) res[i] = av[i]-wv[i];
      return new DoubleArr(res, a.shape);
    }
  }
  private static DNf DNF = new DNf();
  
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) { return call(a, w); }
}