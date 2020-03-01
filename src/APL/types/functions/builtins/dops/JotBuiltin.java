package APL.types.functions.builtins.dops;

import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.*;

public class JotBuiltin extends Dop {
  @Override public String repr() {
    return "∘";
  }
  
  
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    if (ww instanceof Fun) {
      if (aa instanceof Fun) {
        return ((Fun)aa).call((Value)((Fun)ww).call(w));
      } else {
        return ((Fun)ww).call((Value)aa, w);
      }
    } else {
      if (aa instanceof Fun) return ((Fun) aa).call(w, (Value) ww);
      throw new SyntaxError("arr∘arr makes no sense", this);
    }
  }
  public Obj callInv(Obj aa, Obj ww, Value w) {
    if (ww instanceof Fun) {
      if (aa instanceof Fun) {
        return ((Fun)aa).call((Value)((Fun)ww).call(w));
      } else {
        return ((Fun)ww).callInvW((Value)aa, w);
      }
    } else {
      if (aa instanceof Fun) return ((Fun) aa).callInvA(w, (Value) ww);
      throw new SyntaxError("arr∘arr makes no sense", this);
    }
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    if (!(aa instanceof Fun)) {
      throw new SyntaxError("operands of dyadically applied ∘ must be functions, but ⍶ is "+aa.humanType(true), aa, this);
    }
    if (!(ww instanceof Fun)) {
      throw new SyntaxError("operands of dyadically applied ∘ must be functions, but ⍹ is "+ww.humanType(true), ww, this);
    }
    return ((Fun)aa).call(a, (Value)((Fun)ww).call(w));
  }
  public boolean strInv(Obj aa, Obj ww) {
    if (!(ww instanceof Fun)) return false;
    Fun wwf = (Fun) ww;
    return aa instanceof Fun? ((Fun) aa).strInv() && wwf.strInv() : wwf.strInvW();
  }
  public Value strInv(Obj aa, Obj ww, Value w, Value origW) {
    Fun wwf = (Fun) ww;
    if (aa instanceof Fun) {
      Fun aaf = (Fun) aa;
      Value gI = aaf.strInv(w, (Value) wwf.call(origW));
      return wwf.strInv(gI, origW);
    } else {
      return wwf.strInvW((Value) aa, w, origW);
    }
  }
}