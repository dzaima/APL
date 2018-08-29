package APL.types.functions.builtins.dops;

import APL.errors.SyntaxError;
import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;
import APL.types.functions.Dop;

public class OverBuiltin extends Dop {
  public OverBuiltin() {
    super("⍥");
    valid = 0x010;
  }
  
  public Obj call(Obj aa, Obj ww, Value w) {
    throw new SyntaxError("TODO");
  }
  
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    var WW = (Fun) ww;
    return ((Fun)aa).call((Value) WW.call(a), (Value) WW.call(w));
  }
}