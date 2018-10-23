package APL.types.functions.builtins.mops;

import APL.errors.LengthError;
import APL.types.*;
import APL.types.functions.Mop;

import java.util.Arrays;

public class EachBuiltin extends Mop {
  public EachBuiltin() {
    super("¨", 0x011);
  }

  public Obj call(Obj f, Value w) {
    if (w.scalar()) return f instanceof Fun? ((Fun)f).call(w) : f;
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = f instanceof Fun ?
        (Value)((Fun)f).call(w.arr[i])
      : (Value) f;
    }
    return new Arr(n, w.shape);
  }
  public Obj call(Obj f, Value a, Value w) {
    if (w.scalar()) {
      if (a.scalar()) return ((Fun)f).call(a, w);
      Value[] n = new Value[a.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = (Value)((Fun)f).call(a.arr[i], w.first());
      }
      return new Arr(n, a.shape);
    }
    if (a.scalar()) {
      Value[] n = new Value[w.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = (Value)((Fun)f).call(a.first(), w.arr[i]);
      }
      return new Arr(n, w.shape);
    }
    if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes not equal");
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = (Value)((Fun)f).call(a.arr[i], w.arr[i]);
    }
    return new Arr(n, w.shape);
  }
}