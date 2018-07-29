package APL.types.functions.builtins.dops;

import APL.Type;
import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.Dop;

public class JotBuiltin extends Dop {
  public JotBuiltin() {
    super("∘");
    valid = 0x011;
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    if (ww.type == Type.fn) {
      if (aa.type == Type.fn) {
        return ((Fun)aa).call((Value)((Fun)ww).call(w));
      } else {
        return ((Fun)ww).call((Value)aa, w);
      }
    } else {
      if (aa.type == Type.array) throw new SyntaxError("arr∘arr makes no sense");
      //ww is fn
      return ((Fun)aa).call(w, (Value)ww);
    }
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    boolean af = aa.type == Type.fn;
    boolean wf = ww.type == Type.fn;
    if (!af || !wf) throw new SyntaxError("dyadic ∘ requires both operands to be functions");
    return ((Fun)aa).call(a, (Value)((Fun)ww).call(w));
  }
}