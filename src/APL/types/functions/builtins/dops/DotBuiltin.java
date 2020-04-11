package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;
import APL.types.functions.builtins.mops.ReduceBuiltin;

public class DotBuiltin extends Dop {
  @Override public String repr() {
    return ".";
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    Fun wwf = isFn(ww, '⍹');
    return new ReduceBuiltin().derive(aa).call(wwf.call(a, w)); // TODO not lazy
  }
}