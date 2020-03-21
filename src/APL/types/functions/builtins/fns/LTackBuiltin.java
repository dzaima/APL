package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class LTackBuiltin extends Builtin {
  @Override public String repr() {
    return "⊣";
  }
  
  
  
  public Value call(Value w) { return w; }
  public Value call(Value a, Value w) { return a; }
}