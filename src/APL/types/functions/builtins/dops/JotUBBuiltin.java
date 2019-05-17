package APL.types.functions.builtins.dops;

import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.*;

public class JotUBBuiltin extends Dop {
  @Override public String repr() {
    return "⍛";
  }
  
  @Override public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    if (!(aa instanceof Fun)) {
      throw new SyntaxError("both operands of ⍛ must be functions", aa, this);
    }
    if (!(ww instanceof Fun)) {
      throw new SyntaxError("both operands of ⍛ must be functions", ww, this);
    }
    return ((Fun) ww).call((Value) ((Fun) aa).call(a), w);
  }
}
