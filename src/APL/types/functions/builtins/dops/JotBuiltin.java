package APL.types.functions.builtins.dops;

import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.Dop;

public class JotBuiltin extends Dop {
  public JotBuiltin() {
    super("∘", 0x011);
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    if (ww instanceof Fun) {
      if (aa instanceof Fun) {
        return ((Fun)aa).call((Value)((Fun)ww).call(w));
      } else {
        return ((Fun)ww).call((Value)aa, w);
      }
    } else {
      if (aa instanceof Fun) return ((Fun) aa).call(w, (Value) ww);
      throw new SyntaxError("arr∘arr makes no sense", w);
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
      throw new SyntaxError("arr∘arr makes no sense", w);
    }
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    boolean af = aa instanceof Fun;
    boolean wf = ww instanceof Fun;
    if (!af || !wf) throw new SyntaxError("strictly monadic derived function called dyadically", a);
    return ((Fun)aa).call(a, (Value)((Fun)ww).call(w));
  }
}