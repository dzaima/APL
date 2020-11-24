package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.arrs.Rank0Arr;
import APL.types.functions.*;

public class EachLeft extends Mop {
  @Override public String repr() {
    return "ᐵ";
  }
  
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    Fun ff = isFn(f);
    Value[] n = new Value[a.ia];
    for (int i = 0; i < n.length; i++) n[i] = ff.call(a.get(i), w);
    return Arr.createL(n, a.shape);
  }
  
  public Value underW(Obj aa, Obj o, Value a, Value w, DerivedMop derv) {
    return EachBuiltin.underW(isFn(aa), o, a, new Rank0Arr(w), this);
  }
}