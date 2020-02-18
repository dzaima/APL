package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class OverBuiltin extends Dop {
  @Override public String repr() {
    return "⍥";
  }
  
  
  
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    var WW = (Fun) ww;
    return ((Fun)aa).call((Value) WW.call(a), (Value) WW.call(w));
  }
}