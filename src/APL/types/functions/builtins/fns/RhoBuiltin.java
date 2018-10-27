package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.HArr;
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
      if (! (v instanceof Num)) {
        if (v.ia == 0) {
          if (emptyPos == null) emptyPos = i;
          else throw new DomainError("shape contained multiple undefined dimension sizes", v);
        } else throw new DomainError("shape for ⍴ contained "+v.humanType(true), v);
      } else {
        int c = v.asInt();
        sh[i] = c;
        ia *= c;
      }
    }
    if (emptyPos != null) {
      if (w.ia % ia != 0) throw new LengthError("empty dimension not perfect", w);
      sh[emptyPos] = w.ia/ia;
      ia = w.ia;
    } else if (ia == w.ia) return w.ofShape(sh);
    Value[] arr = new Value[ia];
    if (w.ia == 0) {
      var prototype = w.prototype();
      for (int i = 0; i < ia; i++) {
        arr[i] = prototype;
      }
    } else {
      int index = 0;
      for (int i = 0; i < ia; i++) {
        arr[i] = w.get(index++);
        if (index == w.ia) index = 0;
      }
    }
    return new HArr(arr, sh);
  }
}
