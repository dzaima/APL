package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class JotUBBuiltin extends Dop {
  @Override public String repr() {
    return "⍛";
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶'); Fun wwf = isFn(ww, '⍹');
    return wwf.call(aaf.call(a), w);
  }
  
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    Fun aaf = isFn(aa, '⍶'); Fun wwf = isFn(ww, '⍹');
    return wwf.callInvW(aaf.call(a), w);
  }
  
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) {
    Fun aaf = isFn(aa, '⍶'); Fun wwf = isFn(ww, '⍹');
    return aaf.callInv(wwf.callInvA(a, w));
  }
}