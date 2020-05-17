package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class ObverseBuiltin extends Dop {
  @Override public String repr() {
    return "⍫";
  }
  
  
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶');
    return aaf.call(w);
  }
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶');
    return aaf.call(a, w);
  }
  
  public Value callInv(Obj aa, Obj ww, Value w) {
    Fun wwf = isFn(ww, '⍹');
    return wwf.call(w);
  }
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    Fun wwf = isFn(ww, '⍹');
    return wwf.call(a, w);
  }
  
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) { // fall-back to ⍶
    Fun aaf = isFn(aa, '⍶');
    return aaf.callInvA(a, w);
  }
}