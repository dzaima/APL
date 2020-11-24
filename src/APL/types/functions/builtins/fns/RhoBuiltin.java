package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.Arrays;


public class RhoBuiltin extends Builtin {
  @Override public String repr() {
    return "⍴";
  }
  
  
  public Value call(Value w) {
    return Main.toAPL(w.shape);
  }
  public Value call(Value a, Value w) {
    if (a.rank > 1) throw new DomainError("⍴: multidimensional shape (⍴⍺ is "+Main.formatAPL(a.shape)+")", this, a);
    int[] sh;
    int ia;
    Integer emptyPos = null;
    if (a.quickDoubleArr()) {
      sh = a.asIntVec();
      ia = Arr.prod(sh);
    } else {
      sh = new int[a.ia];
      ia = 1;
      for (int i = 0; i < sh.length; i++) {
        Value v = a.get(i);
        if (v instanceof Num) {
          int c = v.asInt();
          sh[i] = c;
          ia *= c;
        } else if (v.ia == 0) {
          if (emptyPos == null) emptyPos = i;
          else throw new DomainError("⍴: shape contained multiple ⍬s", this, v);
        } else throw new DomainError("⍴: shape contained "+v.humanType(true), this, v);
      }
    }
    
    if (emptyPos != null) {
      if (w.ia % ia != 0) {
        StringBuilder b = new StringBuilder();
        for (Value v : a) b.append(v).append(' ');
        b.deleteCharAt(b.length()-1);
        throw new LengthError("⍴: empty dimension not perfect (⍺ ≡ "+b+"; "+(w.ia)+" = ≢⍵)", this, w);
      }
      sh[emptyPos] = w.ia/ia;
      return w.ofShape(sh);
    } else if (ia == w.ia) return w.ofShape(sh);
    
    if (w.ia == 0) {
      return SingleItemArr.maybe(w.prototype(), sh);
      
    } else if (w.scalar()) {
      return SingleItemArr.maybe(w.first(), sh);
      
    } else if (w instanceof BitArr) {
      if (sh.length == 0 && !Main.enclosePrimitives) return w.get(0);
      BitArr wb = (BitArr) w;
      BitArr.BA res = new BitArr.BA(sh);
      int full = ia/wb.ia;
      int frac = ia%wb.ia;
      for (int i = 0; i < full; i++) res.add(wb);
      res.add(wb, 0, frac);
      return res.finish();
    } else if (w.quickDoubleArr()) {
      assert !(w instanceof Primitive);
      if (sh.length == 0 && !Main.enclosePrimitives) return w.get(0);
      double[] inp = w.asDoubleArr();
      double[] res = new double[ia];
      int p = 0;
      for (int i = 0; i < ia; i++) {
        res[i] = inp[p++];
        if (p == w.ia) p = 0;
      }
      return new DoubleArr(res, sh);
    } else if (w instanceof ChrArr) {
      if (sh.length == 0 && !Main.enclosePrimitives) return w.get(0);
      String inp = ((ChrArr) w).s;
      char[] res = new char[ia];
      int p = 0;
      for (int i = 0; i < ia; i++) {
        res[i] = inp.charAt(p++);
        if (p == w.ia) p = 0;
      }
      return new ChrArr(res, sh);
    } else {
      if (sh.length == 0 && w.first() instanceof Primitive && !Main.enclosePrimitives) return w.get(0);
      Value[] arr = new Value[ia];
      int index = 0;
      for (int i = 0; i < ia; i++) {
        arr[i] = w.get(index++);
        if (index == w.ia) index = 0;
      }
      return Arr.create(arr, sh);
    }
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(a, w)) : (Value) o;
    for (int i = 0; i < a.ia; i++) {
      Value c = a.get(i);
      if (!(c instanceof Num)) { // a⍬b ⍴ w - must use all items
        if (w.rank == 0 && v.first() instanceof Primitive) return v.first();
        if (v.ia != w.ia) throw new DomainError("⍢⍴ expected equal amount of output & output items", this);
        return v.ofShape(w.shape);
      }
    }
    int[] sh = a.asIntVec();
    int am = Arr.prod(sh);
    if (am > w.ia) throw new DomainError("⍢("+ Main.formatAPL(sh)+"⍴) applied on array with less items than "+am, this);
    if (!Arrays.equals(sh, v.shape)) throw new DomainError("⍢⍴ expected equal amount of output & output items", this);
    Value[] vs = new Value[w.ia];
    System.arraycopy(v.values(), 0, vs, 0, am);
    System.arraycopy(w.values(), am, vs, am, vs.length-am);
    return Arr.createL(vs, w.shape);
  }
  
  // public Value under(Obj o, Value w) {
  //   Value v = o instanceof Fun? ((Fun) o).call(call(w)) : (Value) o;
  //   int[] sh = v.asIntVec();
  //   
  //   if (Arr.prod(sh) != w.ia) throw new DomainError("⍢⍴ expected equal amount of output & output items", this);
  //   return w.ofShape(sh);
  // }
}