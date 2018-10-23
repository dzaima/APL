package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class CommaBarBuiltin extends Builtin {
  public CommaBarBuiltin() {
    super("⍪", 0x011);
  }
  
  public Obj call(Value w) {
    if (w.rank == 0) return new Arr(w.arr, new int[]{0, 1});
    int[] nsh = new int[]{w.shape[0], w.ia/w.shape[0]};
    return new Arr(w.arr, nsh);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}