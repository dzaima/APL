package APL.types.functions.builtins.fns;

import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

import static APL.Main.compare;

public class LEBuiltin extends Builtin {
  public LEBuiltin() {
    super("≤");
    valid = 0x010;
  }
  
  public Obj call(Value a, Value w) { return vec(a, w); }
  
  public Value scall(Value a, Value w) {
    return compare(a, w)<=0? Num.ONE : Num.ZERO;
  }
}
