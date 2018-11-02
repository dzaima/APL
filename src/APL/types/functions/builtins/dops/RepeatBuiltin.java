package APL.types.functions.builtins.dops;

import APL.types.*;
import APL.types.functions.Dop;

public class RepeatBuiltin extends Dop {
  public RepeatBuiltin() {
    super("⍣", 0x011);
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    int am = ((Num)ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = (Value)((Fun)aa).callInv(w);
      }
    } else for (int i = 0; i < am; i++) {
      w = (Value)((Fun)aa).call(w);
    }
    return w;
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    int am = ((Num)ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = (Value)((Fun)aa).callInvW(a, w);
      }
    } else for (int i = 0; i < am; i++) {
      w = (Value)((Fun)aa).call(a, w);
    }
    return w;
  }
}