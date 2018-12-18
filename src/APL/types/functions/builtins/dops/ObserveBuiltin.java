package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.Dop;

public class ObserveBuiltin extends Dop {
  public ObserveBuiltin() {
    super("⍫", 0x011);
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    return ((Fun) aa).call(w);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    return ((Fun) aa).call(a, w);
  }
  
  public Obj callInv(Obj aa, Obj ww, Value w) {
    return ((Fun) ww).call(w);
  }
  public Obj callInvW(Obj aa, Obj ww, Value a, Value w) {
    return ((Fun) ww).call(a, w);
  }
  
  
}