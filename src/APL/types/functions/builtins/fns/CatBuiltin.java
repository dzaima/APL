package APL.types.functions.builtins.fns;

import APL.errors.LengthError;
import APL.types.*;
import APL.types.functions.Builtin;


public class CatBuiltin extends Builtin {
  public CatBuiltin() {
    super(",");
    valid = 0x011;
    identity = new Arr(Num.ZERO); // TODO not
  }
  public Obj call(Value w) {
    if (w.scalar()) return new Arr(new Value[]{w});
    return new Arr(((Arr)w).arr);
  }
  public Obj call(Value a, Value w) {
    if (a.scalar()) a = new Arr(new Value[]{a.arr[0]});
    if (w.scalar()) w = new Arr(new Value[]{w.arr[0]});
    //if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes not equal", this, w); // TODO not
    for (int i = 0; i < a.rank-1; i++) {
      if (a.shape[i] != w.shape[i]) throw new LengthError("lengths not matchable", this, w);
    }
    int[] newShape = new int[a.rank];
    System.arraycopy(a.shape, 0, newShape, 0, a.rank - 1);
    int chunkSizeA = a.shape[a.rank-1];
    int chunkSizeW = w.shape[w.rank-1];
    newShape[a.rank-1] = chunkSizeA+chunkSizeW;
    int chunks = a.ia/chunkSizeA;
    Value[] arr = new Value[chunks * (chunkSizeA+chunkSizeW)];
    int pos = 0, posA = 0, posW = 0;
    for (int i = 0; i < chunks; i++) {
      for (int j = 0; j < chunkSizeA; j++) {
        arr[pos++] = a.arr[posA++];
      }
      for (int j = 0; j < chunkSizeW; j++) {
        arr[pos++] = w.arr[posW++];
      }
    }
    return new Arr(arr, newShape);
  }
}