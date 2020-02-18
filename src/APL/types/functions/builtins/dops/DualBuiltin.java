package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;

public class DualBuiltin extends Dop {
  @Override public String repr() {
    return "⍢";
  }
  
  
  
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    return under.callInv( (Value) ((Fun)aa).call((Value) under.call(w)));
  }
  public Obj callInv(Obj aa, Obj ww, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    return under.callInv( (Value) ((Fun)aa).callInv((Value) under.call(w)));
  }
  
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    return under.callInv( (Value) ((Fun)aa).call((Value) under.call(a), (Value) under.call(w)));
  }
  public Obj callInvW(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    return under.callInv((Value) ((Fun) aa).callInvW((Value) under.call(a), (Value) under.call(w)));
  }
  public Obj callInvA(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    return under.callInv((Value) ((Fun) aa).callInvA((Value) under.call(a), (Value) under.call(w)));
  }
}