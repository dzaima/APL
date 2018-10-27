package APL.types.functions.builtins.mops;

import APL.errors.LengthError;
import APL.types.*;
import APL.types.arrs.HArr;
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
        (Value)((Fun)f).call(w.get(i))
      : (Value) f;
    }
    return new HArr(n, w.shape);
  }
  public Obj call(Obj f, Value a, Value w) {
    if (w.scalar()) {
      if (a.scalar()) return ((Fun)f).call(a, w);
      Value[] n = new Value[a.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = (Value)((Fun)f).call(a.get(i), w.first());
      }
      return new HArr(n, a.shape);
    }
    if (a.scalar()) {
      Value[] n = new Value[w.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = (Value)((Fun)f).call(a.first(), w.get(i));
      }
      return new HArr(n, w.shape);
    }
    if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes not equal");
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = (Value)((Fun)f).call(a.get(i), w.get(i));
    }
    return new HArr(n, w.shape);
  }
}