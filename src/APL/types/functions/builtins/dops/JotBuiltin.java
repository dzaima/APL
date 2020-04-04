package APL.types.functions.builtins.dops;

import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.*;

public class JotBuiltin extends Dop {
  @Override public String repr() {
    return "∘";
  }
  
  
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    if (ww instanceof Fun) {
      if (aa instanceof Fun) {
        return ((Fun)aa).call(((Fun)ww).call(w));
      } else {
        return ((Fun)ww).call((Value)aa, w);
      }
    } else {
      if (aa instanceof Fun) return ((Fun) aa).call(w, (Value) ww);
      throw new SyntaxError("arr∘arr makes no sense", this);
    }
  }
  public Value callInv(Obj aa, Obj ww, Value w) {
    if (ww instanceof Fun) {
      if (aa instanceof Fun) {
        return ((Fun)ww).callInv(((Fun)aa).callInv(w));
      } else {
        return ((Fun)ww).callInvW((Value)aa, w);
      }
    } else {
      if (aa instanceof Fun) return ((Fun) aa).callInvA(w, (Value) ww);
      throw new SyntaxError("arr∘arr makes no sense", this);
    }
  }
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    if (!(aa instanceof Fun)) {
      throw new SyntaxError("operands of dyadically applied ∘ must be functions, but ⍶ is "+aa.humanType(true), aa, this);
    }
    if (!(ww instanceof Fun)) {
      throw new SyntaxError("operands of dyadically applied ∘ must be functions, but ⍹ is "+ww.humanType(true), ww, this);
    }
    return ((Fun)aa).call(a, ((Fun)ww).call(w));
  }
  
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    return ((Fun) ww).callInv(((Fun) aa).callInvW(a, w));
  }
  
  public Value under(Obj aa, Obj ww, Obj o, Value w, DerivedDop derv) {
    if (ww instanceof Fun) {
      Fun wwf = (Fun) ww;
      if (aa instanceof Fun) {
        Fun gf = (Fun) aa;
        return wwf.under(new Fun() { public String repr() { return gf.repr(); }
          public Value call(Value w) {
            return gf.under(o, w);
          }
        }, w);
      } else {
        return wwf.underW(o, (Value) aa, w);
      }
    } else {
      if (aa instanceof Fun) {
        return ((Fun) aa).underA(o, w, (Value) ww);
      } else {
        throw new SyntaxError("arr∘arr makes no sense", this);
      }
    }
  }
}