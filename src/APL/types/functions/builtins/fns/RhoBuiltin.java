package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

import static APL.Main.toAPL;

public class RhoBuiltin extends Builtin {
  @Override public String repr() {
    return "⍴";
  }
  
  
  public Value call(Value w) {
    
    int[] sh = w.shape;
    //ArrayList<ArrVal> res = new ArrayList<ArrVal>();
    //for (int i = 0; i < sh.length; i++) {
    //  res.add(new Number(sh[i]));
    //}
    return toAPL(sh);
  }
  public Value call(Value a, Value w) {
    if (w.rank > 1) throw new DomainError("multidimensional shape", this, w);
    int[] sh = new int[w.ia];
    int ia = 1;
    Integer emptyPos = null;
    for (int i = 0; i < sh.length; i++) {
      Value v = w.get(i);
      if (v instanceof Num) {
        int c = v.asInt();
        sh[i] = c;
        ia*= c;
      } else if (v.ia == 0) {
        if (emptyPos == null) emptyPos = i;
        else throw new DomainError("shape contained multiple undefined dimension sizes", this, v);
      } else throw new DomainError("shape for ⍴ contained " + v.humanType(true), this, v);
    }
    
    if (emptyPos != null) {
      if (a.ia % ia != 0) throw new LengthError("empty dimension not perfect", this, a);
      sh[emptyPos] = a.ia/ia;
      return a.ofShape(sh);
    } else if (ia == a.ia) return a.ofShape(sh);
    
    if (a.ia == 0) {
      return new SingleItemArr(a.prototype(), sh);
      
    } else if (a.scalar()) {
      return new SingleItemArr(a.first(), sh);
      
    } else if (a instanceof BitArr) {
      if (sh.length == 0 && !Main.enclosePrimitives) return a.get(0);
      BitArr wb = (BitArr) a;
      BitArr.BA res = new BitArr.BA(sh);
      int full = ia/wb.ia;
      int frac = ia%wb.ia;
      for (int i = 0; i < full; i++) res.add(wb);
      res.add(wb, 0, frac);
      return res.finish();
    } else if (a.quickDoubleArr()) {
      assert !(a instanceof Primitive);
      if (sh.length == 0 && !Main.enclosePrimitives) return a.get(0);
      double[] inp = a.asDoubleArr();
      double[] res = new double[ia];
      int p = 0;
      for (int i = 0; i < ia; i++) {
        res[i] = inp[p++];
        if (p == a.ia) p = 0;
      }
      return new DoubleArr(res, sh);
    } else {
      if (sh.length == 0 && a.first() instanceof Primitive && !Main.enclosePrimitives) return a.get(0);
      Value[] arr = new Value[ia];
      int index = 0;
      for (int i = 0; i < ia; i++) {
        arr[i] = a.get(index++);
        if (index == a.ia) index = 0;
      }
      return Arr.create(arr, sh);
    }
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(w, a)) : (Value) o;
    for (int i = 0; i < w.ia; i++) {
      Value c = w.get(i);
      if (!(c instanceof Num)) { // a⍬b ⍴ w - must use all items
        if (a.rank == 0 && v.first() instanceof Primitive) return v.first();
        if (v.ia != a.ia) throw new DomainError("⍢⍴ expected equal amount of output & output items");
        return v.ofShape(a.shape);
      }
    }
    int[] sh = w.asIntVec();
    int am = Arr.prod(sh);
    if (am > a.ia) throw new DomainError("⍢("+ Main.formatAPL(sh)+"⍴) applied on array with less items than "+am, this);
    if (!Arrays.equals(sh, v.shape)) throw new DomainError("⍢⍴ expected equal amount of output & output items", this);
    Value[] vs = new Value[a.ia];
    System.arraycopy(v.values(), 0, vs, 0, am);
    System.arraycopy(a.values(), am, vs, am, vs.length-am);
    return Arr.createL(vs, a.shape);
  }
  
  // public Value under(Obj o, Value w) {
  //   Value v = o instanceof Fun? ((Fun) o).call(call(w)) : (Value) o;
  //   int[] sh = v.asIntVec();
  //   
  //   if (Arr.prod(sh) != w.ia) throw new DomainError("⍢⍴ expected equal amount of output & output items", this);
  //   return w.ofShape(sh);
  // }
}