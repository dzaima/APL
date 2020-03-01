package APL.types.functions.builtins.dops;

import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.*;

public class JotDiaeresisBuiltin extends Dop {
  @Override public String repr() {
    return "⍤";
  }
  
  
  
  @Override
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    return ((Fun) aa).call((Value) ((Fun) ww).call(a, w));
  }
  
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    return ((Fun) aa).call((Value) ((Fun) ww).call(w));
  }
  
  public Obj callInv(Obj aa, Obj ww, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    return ((Fun) aa).call((Value) ((Fun) ww).call(w));
  }
}