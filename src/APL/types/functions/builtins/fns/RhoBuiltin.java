package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import static APL.Main.toAPL;

public class RhoBuiltin extends Builtin {
  public RhoBuiltin() {
    super("⍴", 0x011);
  }
  public Obj call(Value w) {

    int[] sh = w.shape;
    //ArrayList<ArrVal> res = new ArrayList<ArrVal>();
    //for (int i = 0; i < sh.length; i++) {
    //  res.add(new Number(sh[i]));
    //}
    return toAPL(sh);
  }
  public Obj call(Value a, Value w) {
    if (a.rank > 1) throw new DomainError("multidimensional shape", a);
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
        else throw new DomainError("shape contained multiple undefined dimension sizes", v);
      } else throw new DomainError("shape for ⍴ contained " + v.humanType(true), v);
    }
    if (emptyPos != null) {
      if (w.ia % ia != 0) throw new LengthError("empty dimension not perfect", w);
      sh[emptyPos] = w.ia/ia;
      return w.ofShape(sh);
    } else if (ia == w.ia) return w.ofShape(sh);
    if (w.ia == 0) {
      return new SingleItemArr(w.prototype(), sh);
    } else if (w.quickDoubleArr()) {
      if (sh.length == 0 && !Main.enclosePrimitives) return w.get(0);
      double[] inp = w.asDoubleArr();
      double[] res = new double[ia];
      int p = 0;
      for (int i = 0; i < ia; i++) {
        res[i] = inp[p++];
        if (p == w.ia) p = 0;
      }
      return new DoubleArr(res, sh);
    } else if (w.scalar()) {
      return new SingleItemArr(w, sh);
    } else {
      if (sh.length == 0 && w.get(0).primitive() && !Main.enclosePrimitives) return w.get(0);
      Value[] arr = new Value[ia];
      int index = 0;
      for (int i = 0; i < ia; i++) {
        arr[i] = w.get(index++);
        if (index == w.ia) index = 0;
      }
      return new HArr(arr, sh);
    }
  }
}
