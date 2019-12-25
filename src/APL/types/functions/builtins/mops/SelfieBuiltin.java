package APL.types.functions.builtins.mops;

import APL.errors.DomainError;
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
  
  @Override public Obj callInvW(Obj f, Value a, Value w) {
    if (f instanceof Fun) return ((Fun) f).callInvA(w, a);
    throw new DomainError("A⍨ cannot be inverted", this);
  }
  
  @Override public Obj callInvA(Obj f, Value a, Value w) {
    if (f instanceof Fun) return ((Fun) f).callInvW(w, a);
    throw new DomainError("A⍨ cannot be inverted", this);
  }
}