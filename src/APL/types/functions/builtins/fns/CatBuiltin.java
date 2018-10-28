package APL.types.functions.builtins.fns;

import APL.errors.LengthError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;


public class CatBuiltin extends Builtin {
  public CatBuiltin() {
    super(",", 0x011);
  }
  public Obj call(Value w) {
    if (w instanceof Primitive) return new Shape1Arr(w);
    return w.ofShape(new int[]{w.ia});
  }
  public Obj call(Value a, Value w) {
    if (a.scalar()) a = new Shape1Arr(a.get(0));
    if (w.scalar()) w = new Shape1Arr(w.get(0));
    for (int i = 0; i < a.rank-1; i++) {
      if (a.shape[i] != w.shape[i]) throw new LengthError("lengths not matchable", w);
    }
    int[] newShape = new int[a.rank];
    System.arraycopy(a.shape, 0, newShape, 0, a.rank - 1);
    int chunkSizeA = a.shape[a.rank-1];
    if (chunkSizeA==0) {
      return w; //new HArr(new Value[0], newShape);
    }
    int chunkSizeW = w.shape[w.rank-1];
    newShape[a.rank-1] = chunkSizeA+chunkSizeW;
    int chunks = a.ia/chunkSizeA;
    Value[] arr = new Value[chunks * (chunkSizeA+chunkSizeW)];
    int pos = 0, posA = 0, posW = 0;
    Value[] av = a.values();
    Value[] wv = w.values();
    for (int i = 0; i < chunks; i++) {
      if (chunkSizeA >= 0) System.arraycopy(av, posA, arr, pos, chunkSizeA);
      pos+= chunkSizeA;
      posA+= chunkSizeA;
      if (chunkSizeW >= 0) System.arraycopy(wv, posW, arr, pos, chunkSizeW);
      pos+= chunkSizeW;
      posW+= chunkSizeW;
    }
    return new HArr(arr, newShape);
  }
}