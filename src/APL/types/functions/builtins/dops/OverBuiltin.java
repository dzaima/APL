package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.Dop;

public class OverBuiltin extends Dop {
  @Override public String repr() {
    return "⍥";
  }
  
  
  
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    var WW = (Fun) ww;
    return ((Fun)aa).call((Value) WW.call(a), (Value) WW.call(w));
  }
}