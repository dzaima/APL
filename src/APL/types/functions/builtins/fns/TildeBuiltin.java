package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.functions.Builtin;

public class TildeBuiltin extends Builtin {
  public TildeBuiltin(Scope sc) {
    super("~", sc);
  }
  
  class Nf implements NumMV {
    public Value call(Num w) {
      return Main.bool(w.num, sc)? Num.ZERO : Num.ONE;
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Main.bool(a[i], sc)? 0 : 1;
    }
  }
  private final Nf NF = new Nf();
  
  public Obj call(Value w) {
    return numM(NF, w);
  }
  
  public Obj call(Value a, Value w) {
    int ia = 0;
    boolean[] leave = new boolean[a.ia];
    a: for (int i = 0; i < a.ia; i++) {
      Value v = a.get(i);
      for (var c : w) {
        if (v.equals(c)) continue a;
      }
      leave[i] = true;
      ia++;
    }
    Value[] res = new Value[ia];
    int pos = 0;
    for (int i = 0; i < leave.length; i++) {
      if (leave[i]) {
        res[pos++] = a.get(i);
      }
    }
    return Arr.create(res);
  }
}