package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class LTackBuiltin extends Builtin {
  public LTackBuiltin () {
    super("⊣");
  }
  
  public Obj call(Value w) { return w; }
  public Obj call(Value a, Value w) { return a; }
}