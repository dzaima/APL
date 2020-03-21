package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class JotUBBuiltin extends Dop {
  @Override public String repr() {
    return "⍛";
  }
  
  @Override public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    return ((Fun) ww).call((Value) ((Fun) aa).call(a), w);
  }
}
