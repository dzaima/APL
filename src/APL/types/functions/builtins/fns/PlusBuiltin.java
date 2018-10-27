package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;


public class PlusBuiltin extends Builtin {
  public PlusBuiltin() {
    super("+", 0x011);
  }

  public Obj call(Value w) {
    return scalar(v -> {
      if (!(v instanceof Num)) throw new DomainError("Conjugating a non-number", w); // TODO decide whether this should exist
      return ((Num)v).conjugate();
    }, w);
  }
  
  static class DNf implements DyNumVecFun {
    public Value call(Num a, Num w) {
      return a.plus(w);
    } 
    public Arr call(Num a, DoubleArr w) {
      double[] res = new double[w.ia]; double   av = a.num; double[] wv = w.arr;
      for (int i = 0; i < w.ia; i++) res[i] = av   +wv[i];
      return new DoubleArr(res, w.shape);
    }
    public Arr call(DoubleArr a, Num w) {
      double[] res = new double[a.ia]; double[] av = a.arr; double   wv = w.num;
      for (int i = 0; i < w.ia; i++) res[i] = av[i]+wv   ;
      return new DoubleArr(res, a.shape);
    }
    public Arr call(DoubleArr a, DoubleArr w) {
      double[] res = new double[a.ia]; double[] av = a.arr; double[] wv = w.arr;
      for (int i = 0; i < w.ia; i++) res[i] = av[i]+wv[i];
      return new DoubleArr(res, a.shape);
    }
  }
  private static DNf DNF = new DNf();
  public Obj call(Value a0, Value w0) {
    return scalarNum(DNF, a0, w0);
  }
  public Obj callInv(Value w) { return call(w); }
  public Obj callInvW(Value a, Value w) {
    try {
      return new MinusBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError("", e.cause);
    }
  }
}