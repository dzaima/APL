package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class CompareBuiltin extends Builtin {
  public CompareBuiltin () {
    super("⌺", 0x010);
  }
  public Obj call(Value a, Value w) {
    return new Num(a.compareTo(w));
  }
}