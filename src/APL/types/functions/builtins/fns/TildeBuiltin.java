package APL.types.functions.builtins.fns;

import APL.Main;
import APL.types.*;
import APL.types.functions.Builtin;

public class TildeBuiltin extends Builtin {
  public TildeBuiltin() {
    super("~");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return Main.bool(w, sc)? Num.ZERO : Num.ONE;
  }
  
  public Obj call(Value a, Value w) {
    int ia = 0;
    boolean[] leave = new boolean[a.ia];
    a: for (int i = 0; i < a.ia; i++) {
      Value v = a.arr[i];
      for (var c : w.arr) {
        if (v.equals(c)) continue a;
      }
      leave[i] = true;
      ia++;
    }
    Value[] res = new Value[ia];
    int pos = 0;
    for (int i = 0; i < leave.length; i++) {
      if (leave[i]) {
        res[pos++] = a.arr[i];
      }
    }
    return new Arr(res);
  }
}