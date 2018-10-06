package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.functions.*;

public class KeyBuiltin extends Mop {
  public KeyBuiltin() {
    super("⌸");
    valid = 0x011;
  }
  
  public Obj call(Obj aa, Value w) {
    if (aa instanceof APLMap) {
      if (w.rank > 1) {
        Value[] arr = new Value[w.ia];
        for (int i = 0; i < w.arr.length; i++) {
          arr[i] = (Value) ((APLMap) aa).getRaw(w.arr[i]);
        }
        return new Arr(arr, w.shape);
      }
      return ((APLMap) aa).getRaw(w);
    }
    throw null; // TODO
  }
  
  public Obj call(Obj aa, Value a, Value w) {
    if (aa instanceof APLMap) {
      ((APLMap)aa).set(a, w);
      return w;
    }
    throw null;
  }
}