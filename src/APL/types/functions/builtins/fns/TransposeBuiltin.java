package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class TransposeBuiltin extends Builtin {
  @Override public String repr() {
    return "⍉";
  }
  
  
  
  public Value call(Value w) {
    if (w.scalar()) return w;
    if (w instanceof DoubleArr) {
      double[] dw = w.asDoubleArr();
      double[] res = new double[w.ia];
      int[] sh = new int[w.rank];
      for (int i = 0; i < w.rank; i++) {
        sh[i] = w.shape[w.rank - i - 1];
      }
      if (w.rank == 2) {
        int ww = w.shape[0];
        int wh = w.shape[1];
        int ip = 0;
        for (int x = 0; x < ww; x++) {
          int op = x;
          for (int y = 0; y < wh; y++) {
            res[op] = dw[ip++];
            op+= ww;
          }
        }
      } else {
        int ci = 0;
        for (int[] c : new Indexer(w.shape, 0)) {
          int[] nc = new int[w.rank];
          for (int i = 0; i < w.rank; i++) {
            nc[i] = c[w.rank - i - 1];
          }
          res[Indexer.fromShape(sh, nc, 0)] = dw[ci++];
        }
      }
      return new DoubleArr(res, sh);
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
  public Value callInv(Value w) {
    return call(w);
  }
  
  public boolean strInv() { return true; }
  
  public Value strInv(Value w, Value origW) {
    return call(w);
  }
}