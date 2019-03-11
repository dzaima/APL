package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class JotDiaeresisBuiltin extends Dop {
  @Override public String repr() {
    return "⍤";
  }
  
  
  
  @Override
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    return ((Fun) aa).call((Value) ((Fun) ww).call(a, w));
  }
}
