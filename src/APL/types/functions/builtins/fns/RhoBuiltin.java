package APL.types.functions.builtins.fns;

import APL.*;
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
    if (a.rank > 1) throw new DomainError("multidimensional shape", this, a);
    int[] sh = new int[a.ia];
    int ia = 1;
    Integer emptyPos = null;
    for (int i = 0; i < sh.length; i++) {
      Value v = a.get(i);
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
      if (w.ia % ia != 0) throw new LengthError("empty dimension not perfect", this, w);
      sh[emptyPos] = w.ia/ia;
      return w.ofShape(sh);
    } else if (ia == w.ia) return w.ofShape(sh);
    
    if (w.ia == 0) {
      return new SingleItemArr(w.prototype(), sh);
      
    } else if (w.scalar()) {
      return new SingleItemArr(w.first(), sh);
    
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
        if (v.ia != w.ia) throw new DomainError("⍢⍴ expected equal amount of output & output items");
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
