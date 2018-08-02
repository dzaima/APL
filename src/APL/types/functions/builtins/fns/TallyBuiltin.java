package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import static APL.Main.up;

public class TallyBuiltin extends Builtin {
  public TallyBuiltin() {
    super("⍬");
    valid = 0x011;
  }
  public Obj call(Value w) {
    if (w.scalar()) return Num.ONE;
    return new Num(((Arr)w).shape[0]);
  }
  public Obj call(Value a, Value w) {
    throw up;
  }
}