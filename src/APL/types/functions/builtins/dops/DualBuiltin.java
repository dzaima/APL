package APL.types.functions.builtins.dops;

import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Dop;

public class DualBuiltin extends Dop {
  public DualBuiltin() {
    super("⍢");
  }
  
  public Obj call(Obj aa, Obj ww, Value w) {
    Fun under = (Fun) ww;
    return under.callInv( (Value) ((Fun)aa).call((Value) under.call(w)));
  }
  
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    Fun under = (Fun) ww;
    return under.callInv( (Value) ((Fun)aa).call((Value) under.call(a), (Value) under.call(w)));
  }
}