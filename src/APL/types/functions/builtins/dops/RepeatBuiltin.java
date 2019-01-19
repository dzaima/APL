package APL.types.functions.builtins.dops;

import APL.*;
import APL.types.*;
import APL.types.functions.Dop;

public class RepeatBuiltin extends Dop {
  @Override public String repr() {
    return "⍣";
  }
  
  public RepeatBuiltin(Scope sc) {
    super(sc);
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    Fun f = (Fun) aa;
    if (ww instanceof Fun) {
      Fun g = (Fun) ww;
      Value prev = w;
      Value curr = (Value) f.call(w);
      while (!Main.bool(g.call(prev, curr), sc)) {
        Value next = (Value) f.call(curr);
        prev = curr;
        curr = next;
      }
      return curr;
    } else {
      int am = ((Num) ww).asInt();
      if (am < 0) {
        for (int i = 0; i < -am; i++) {
          w = (Value) ((Fun) aa).callInv(w);
        }
      } else for (int i = 0; i < am; i++) {
        w = (Value) ((Fun) aa).call(w);
      }
      return w;
    }
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