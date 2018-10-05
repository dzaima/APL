package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

import static APL.Main.human;
import static APL.Main.toAPL;

public class RhoBuiltin extends Builtin {
  public RhoBuiltin() {
    super("⍴");
    valid = 0x011;
  }
  public Obj call(Value w) {

    int[] sh = w.shape;
    //ArrayList<ArrVal> res = new ArrayList<ArrVal>();
    //for (int i = 0; i < sh.length; i++) {
    //  res.add(new Number(sh[i]));
    //}
    return toAPL(sh);
  }
  public Obj call(Value a, Value w) { // TODO ⍬ 2 ⍴ ⍳6
    if (a.rank > 1) throw new DomainError("multidimensional shape", this, a);
    int[] sh = new int[a.arr.length];
    int ia = 1;
    Integer emptyPos = null;
    for (int i = 0; i < sh.length; i++) {
      Value v = a.arr[i];
      if (! (v instanceof Num)) {
        if (v.ia == 0) {
          if (emptyPos == null) emptyPos = i;
          else throw new DomainError("shape contained multiple undefined dimension sizes", this, v);
        } else throw new DomainError("shape for ⍴ contained "+human(v.valtype, true), this, v);
      } else {
        int c = ((Num) v).intValue();
        sh[i] = c;
        ia *= c;
      }
    }
    if (emptyPos != null) {
//      System.out.println(w.ia+" "+ia);
      if (w.ia % ia != 0) throw new LengthError("empty dimension not perfect", this, w);
      sh[emptyPos] = w.ia/ia;
      ia = w.ia;
    }
    Value[] arr = new Value[ia];
    if (w.ia == 0) {
      var prototype = w.prototype;
      for (int i = 0; i < ia; i++) {
        arr[i] = prototype;
      }
    } else {
      int index = 0;
      for (int i = 0; i < ia; i++) {
        arr[i] = w.arr[index++];
        if (index == w.ia) index = 0;
      }
    }
    return new Arr(arr, sh, w.prototype);
  }
}
