package APL.types.functions.builtins.dops;

import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.*;

public class DualBuiltin extends Dop {
  @Override public String repr() {
    return "⍢";
  }
  
  
  
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    isFn(ww, '⍹');
    Fun under = (Fun) ww;
    Value sub = under.call(w);
    if (under.strInv()) {
      Value obj = aa instanceof Value? (Value) aa : ((Fun) aa).call(sub);
      return under.strInv(obj, w);
    }
    
    if (!(aa instanceof Fun)) throw new SyntaxError("⍶ of computational ⍢ must be a function", this);
    Value obj = ((Fun) aa).call(sub);
    return under.callInv(obj);
  }
  public Value callInv(Obj aa, Obj ww, Value w) {
    isFn(ww, '⍹');
    Fun under = (Fun) ww;
    Value sub = under.call(w);
    if (under.strInv()) {
      Value obj = aa instanceof Value? (Value) aa : ((Fun) aa).callInv(sub);
      return under.strInv(obj, w);
    }
  
    if (!(aa instanceof Fun)) throw new SyntaxError("⍶ of computational ⍢ must be a function", this);
    Value obj = ((Fun) aa).callInv(sub);
    return under.callInv(obj);
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    Value aS = under.call(a);
    Value wS = under.call(w);
    if (under.strInv()) {
      Value obj = ((Fun) aa).call(aS, wS);
      return under.strInv(obj, w);
    }
    
    return under.callInv(((Fun)aa).call(aS, wS));
  }
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    return under.callInv(((Fun) aa).callInvW(under.call(a), under.call(w)));
  }
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    Fun under = (Fun) ww;
    return under.callInv(((Fun) aa).callInvA(under.call(a), under.call(w)));
  }
}