package APL.types.functions.builtins.mops;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.SingleItemArr;
import APL.types.functions.*;

import java.util.Arrays;

public class EachBuiltin extends Mop {
  @Override public String repr() {
    return "¨";
  }
  
  
  
  public Obj call(Obj f, Value w, DerivedMop derv) {
    if (w.scalar()) return f instanceof Fun? ((Fun)f).call(w.first()) : f;
    if (f instanceof Fun) {
      Value[] n = new Value[w.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = ((Value) ((Fun) f).call(w.get(i))).squeeze();
      }
      return Arr.create(n, w.shape);
    } else {
      return new SingleItemArr(((Value) f), w.shape);
    }
  }
  public Obj call(Obj f, Value a, Value w, DerivedMop derv) {
    if (w.scalar()) {
      if (a.scalar()) return ((Fun)f).call(a, w);
      Value[] n = new Value[a.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = ((Value)((Fun)f).call(a.get(i), w.first())).squeeze();
      }
      return Arr.create(n, a.shape);
    }
    if (a.scalar()) {
      Value[] n = new Value[w.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = ((Value)((Fun)f).call(a.first(), w.get(i))).squeeze();
      }
      return Arr.create(n, w.shape);
    }
    if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes not equal");
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ((Value)((Fun)f).call(a.get(i), w.get(i))).squeeze();
    }
    return Arr.create(n, w.shape);
  }
  
  @Override public Obj callInv(Obj f, Value w) {
    if (!(f instanceof Fun)) throw new DomainError("can't invert A¨");
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ((Value) ((Fun) f).callInv(w.get(i))).squeeze();
    }
    return Arr.create(n, w.shape);
  }
}