package APL.types.functions.builtins.mops;

import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.*;

public class ScanBuiltin extends Mop {
  @Override public String repr() {
    return "\\";
  }
  
  public Obj call(Obj aa, Value w, DerivedMop derv) {
    // TODO ranks
    if (! (aa instanceof Fun)) throw new SyntaxError("\\ expects ⍶ to be a function");
    Fun f = (Fun) aa;
    if (w.ia == 0) return w;
    Value c = w.get(0);
    Value[] res = new Value[w.ia];
    res[0] = c;
    for (int i = 1; i<w.ia; i++) {
      c = (Value) f.call(c, w.get(i));
      res[i] = c;
    }
    return Arr.create(res);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}