package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.functions.*;

public class EachRight extends Mop {
  @Override public String repr() {
    return "ᑈ";
  }
  
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    Fun ff = isFn(f);
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ff.call(a, w.get(i)).squeeze();
    }
    return Arr.createL(n, w.shape);
  }
}