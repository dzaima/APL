package APL.types.functions.builtins.fns;

import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class CeilingBuiltin extends Builtin {
  public CeilingBuiltin() {
    super("⌈");
    valid = 0x11;
  }
  public Obj call(Value w) { return vec(w); }
  public Obj call(Value a, Value w) { return vec(a, w); }
  
  public Value scall(Value w) {
    return ((Num)w).ceil();
  }
  
  public Value scall(Value a, Value w) {
    return Num.max((Num)a, (Num)w);
  }
}