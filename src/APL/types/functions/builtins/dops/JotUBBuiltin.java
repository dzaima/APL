package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class JotUBBuiltin extends Dop {
  @Override public String repr() {
    return "⍛";
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    return ((Fun) ww).call(((Fun) aa).call(a), w);
  }
  
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun wwf = (Fun) ww;
    Fun aaf = (Fun) aa;
    return wwf.callInvW(aaf.call(a), w);
  }
  
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) {
    return ((Fun) aa).callInv(((Fun) ww).callInvA(a, w));
  }
}
