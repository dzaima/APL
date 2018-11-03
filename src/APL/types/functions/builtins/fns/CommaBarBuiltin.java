package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class CommaBarBuiltin extends Builtin {
  public CommaBarBuiltin() {
    super("⍪", 0x011);
  }
  
  public Obj call(Value w) {
    if (w.rank==1 && w.shape[0]==0) return new EmptyArr(new int[]{0, 1});
    if (w.rank==0) return new HArr(w.values(), new int[]{1, 1});
    int[] nsh = new int[]{w.shape[0], w.ia/w.shape[0]};
    return w.ofShape(nsh);
  }
  
  public Obj call(Value a, Value w) {
    return CatBuiltin.cat(a, w, 0);
  }
}
