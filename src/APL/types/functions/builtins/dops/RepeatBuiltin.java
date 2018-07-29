package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.Dop;

public class RepeatBuiltin extends Dop {
  public RepeatBuiltin() {
    super("⍣");
    valid = 0x011;
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    int am = ((Num)ww).intValue();
    for (int i = 0; i < am; i++) {
      w = (Value)((Fun)aa).call(w);
    }
    return w;
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    int am = ((Num)ww).intValue();
    for (int i = 0; i < am; i++) {
      w = (Value)((Fun)aa).call(a, w);
    }
    return w;
  }
}