package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class ObserveBuiltin extends Dop {
  @Override public String repr() {
    return "⍫";
  }
  
  
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    isFn(aa, '⍶');
    return ((Fun) aa).call(w);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶');
    return ((Fun) aa).call(a, w);
  }
  
  public Obj callInv(Obj aa, Obj ww, Value w) {
    isFn(ww, '⍹');
    return ((Fun) ww).call(w);
  }
  public Obj callInvW(Obj aa, Obj ww, Value a, Value w) {
    isFn(ww, '⍹');
    return ((Fun) ww).call(a, w);
  }
  
  
}