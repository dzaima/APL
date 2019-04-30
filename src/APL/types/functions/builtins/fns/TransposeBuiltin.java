package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class TransposeBuiltin extends Builtin {
  @Override public String repr() {
    return "⍉";
  }
  
  
  
  public Obj call(Value w) {
    if (w instanceof DoubleArr) {
      double[] dw = w.asDoubleArr();
      double[] res = new double[w.ia];
      int[] ns = new int[w.rank];
      for (int i = 0; i < w.rank; i++) {
        ns[i] = w.shape[w.rank - i - 1];
      }
      int ci = 0;
      for (int[] c : new Indexer(w.shape, 0)) {
        int[] nc = new int[w.rank];
        for (int i = 0; i < w.rank; i++) {
          nc[i] = c[w.rank - i - 1];
        }
        res[Indexer.fromShape(ns, nc, 0)] = dw[ci++];
      }
      return new DoubleArr(res, ns);
    }
    Value[] arr = new Value[w.ia];
    int[] ns = new int[w.rank];
    for (int i = 0; i < w.rank; i++) {
      ns[i] = w.shape[w.rank - i - 1];
    }
    for (int[] c : new Indexer(w.shape, 0)) {
      int[] nc = new int[w.rank];
      for (int i = 0; i < w.rank; i++) {
        nc[i] = c[w.rank - i - 1];
      }
      arr[Indexer.fromShape(ns, nc, 0)] = w.simpleAt(c);
    }
    return Arr.create(arr, ns);
  }
  @Override public Obj callInv(Value w) {
    return call(w);
  }
}