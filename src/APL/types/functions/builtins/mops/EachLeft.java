package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.functions.*;

public class EachLeft extends Mop {
  @Override public String repr() {
    return "⍄";
  }
  
  public Obj call(Obj f, Value a, Value w, DerivedMop derv) {
    if (a.scalar()) return ((Fun) f).call(a, w);
    Value[] n = new Value[a.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ((Value)((Fun) f).call(a.get(i), w)).squeeze();
    }
    return Arr.create(n, w.shape);
  }
}
