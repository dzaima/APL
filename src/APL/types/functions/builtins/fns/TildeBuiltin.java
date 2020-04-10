package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class TildeBuiltin extends Builtin {
  @Override public String repr() {
    return "~";
  }
  public Value call(Value w) { return rec(w); }
  
  private Value rec(Value w) {
    if (w instanceof Arr) {
      if (w instanceof BitArr) {
        BitArr wb = (BitArr) w;
        long[] res = new long[wb.llen()];
        for (int i = 0; i < res.length; i++) res[i] = ~wb.arr[i];
        return new BitArr(res, w.shape);
      }
      
      if (w.quickDoubleArr()) {
        // for (int i = 0; i < w.length; i++) if (w[i] == 0) res[i>>6]|= 1L << (i&63);
        BitArr.BA a = new BitArr.BA(w.shape);
        for (double v : w.asDoubleArr()) a.add(v == 0);
        return a.finish();
      }
      
      Arr o = (Arr) w;
      if (o.ia>0 && o.get(0) instanceof Num) {
        BitArr.BA a = new BitArr.BA(w.ia); // it's probably worth going all-in on creating a bitarr
        for (int i = 0; i < o.ia; i++) {
          Value v = o.get(i);
          if (v instanceof Num) a.add(!Main.bool(v));
          else {
            a = null;
            break;
          }
        }
        if (a != null) return a.finish();
        // could make it reuse the progress made, but ¯\_(ツ)_/¯
      }
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = rec(o.get(i));
      }
      return new HArr(arr, o.shape);
    } else if (w instanceof Num) return Main.bool(w)? Num.ZERO : Num.ONE;
    else throw new DomainError("Expected boolean, got "+w.humanType(false), this, w);
  }
  
  public static BitArr call(BitArr w) {
    BitArr.BC bc = BitArr.create(w.shape);
    for (int i = 0; i < bc.arr.length; i++) {
      bc.arr[i] = ~w.arr[i];
    }
    return bc.finish();
  }
  
  public Value call(Value a, Value w) {
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