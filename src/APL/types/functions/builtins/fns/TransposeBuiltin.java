package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.types.*;
import APL.types.functions.Builtin;

public class TransposeBuiltin extends Builtin {
  public TransposeBuiltin() {
    super("⍉");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
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
      arr[Indexer.fromShape(ns, nc, 0)] = ((Arr)w).simpleAt(c);
    }
    return new Arr(arr, ns);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}