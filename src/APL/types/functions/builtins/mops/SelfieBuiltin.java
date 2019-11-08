package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.functions.*;

public class SelfieBuiltin extends Mop {
  @Override public String repr() {
    return "⍨";
  }
  
  

  public Obj call(Obj f, Value w, DerivedMop derv) {
    if (f instanceof Fun) return ((Fun)f).call(w, w);
    return f;
  }
  public Obj call(Obj f, Value a, Value w, DerivedMop derv) {
    if (f instanceof Fun) return ((Fun)f).call(w, a);
    return f;
  }
}