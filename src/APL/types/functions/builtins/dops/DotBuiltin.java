package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.*;
import APL.types.functions.builtins.mops.ReduceBuiltin;

import static APL.Main.up;

public class DotBuiltin extends Dop {
  @Override public String repr() {
    return ".";
  }
  
  
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    throw up;
  }
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    return new ReduceBuiltin().derive(aa).call((Value) ((Fun) ww).call(a, w)); // TODO not lazy
  }
}