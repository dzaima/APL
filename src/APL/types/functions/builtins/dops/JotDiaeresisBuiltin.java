package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.Dop;

public class JotDiaeresisBuiltin extends Dop {
  public JotDiaeresisBuiltin() {
    super("⍤");
  }
  
  @Override
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    return ((Fun) aa).call((Value) ((Fun) ww).call(a, w));
  }
}
