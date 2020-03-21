package APL.types.functions.builtins.mops;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.SingleItemArr;
import APL.types.functions.*;

import java.util.Arrays;

public class EachBuiltin extends Mop {
  @Override public String repr() {
    return "¨";
  }
  
  
  
  public Value call(Obj f, Value w, DerivedMop derv) {
    if (w.scalar()) return f instanceof Fun? ((Fun)f).call(w.first()) : (Value) f;
    if (f instanceof Fun) {
      Value[] n = new Value[w.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = ((Fun) f).call(w.get(i)).squeeze();
      }
      return Arr.create(n, w.shape);
    } else {
      return new SingleItemArr((Value) f, w.shape);
    }
  }
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    if (w.scalar()) {
      if (a.scalar()) return ((Fun)f).call(a, w);
      Value[] n = new Value[a.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = ((Fun)f).call(a.get(i), w.first()).squeeze();
      }
      return Arr.create(n, a.shape);
    }
    if (a.scalar()) {
      Value[] n = new Value[w.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = ((Fun)f).call(a.first(), w.get(i)).squeeze();
      }
      return Arr.create(n, w.shape);
    }
    if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes not equal ("+ Main.formatAPL(a.shape)+" vs "+Main.formatAPL(w.shape)+")");
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ((Fun)f).call(a.get(i), w.get(i)).squeeze();
    }
    return Arr.create(n, w.shape);
  }
  
  public Value callInv(Obj f, Value w) {
    if (!(f instanceof Fun)) throw new DomainError("can't invert A¨");
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ((Fun) f).callInv(w.get(i)).squeeze();
    }
    if (w.rank == 0 && n[0] instanceof Primitive) return n[0];
    return Arr.create(n, w.shape);
  }
  
  public boolean strInv(Obj f) {
    return f instanceof Fun && ((Fun) f).strInv();
  }
  public Value strInv(Obj f, Value w, Value origW) {
    Fun ff = (Fun) f;
    Value[] res = new Value[origW.ia];
    for (int i = 0; i < res.length; i++) {
      res[i] = ff.strInv(w.get(i), origW.get(i));
    }
    return Arr.create(res, origW.shape);
  }
  public boolean strInvW(Obj f) {
    return f instanceof Fun && ((Fun) f).strInvW();
  }
  public Value strInvW(Obj f, Value a, Value w, Value origW) {
    Fun ff = (Fun) f;
    Value[] res = new Value[origW.ia];
    for (int i = 0; i < res.length; i++) {
      res[i] = ff.strInvW(a, w.get(i), origW.get(i));
    }
    return Arr.create(res, origW.shape);
  }
}