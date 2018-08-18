package APL.types.functions.builtins.fns;

import APL.Main;
import APL.types.*;
import APL.types.functions.Builtin;

public class StarBuiltin extends Builtin {
  public StarBuiltin() {
    super("*");
    valid = 0x011;
  }
  
  public Obj call(Value w) { return vec(w); }
  public Obj call(Value a, Value w) { return vec(a, w); }
  
  protected Value scall(Value w) {
    return Num.E.pow((Num) w);
  }
  protected Value scall(Value a, Value w) {
    return ((Num)a).pow((Num)w);
  }
}