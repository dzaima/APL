package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

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
  public Obj call(Value a, Value w) {
    if (a.rank > 1) throw new DomainError("multidimensional shape");
    int[] sh = new int[a.arr.length];
    int ia = 1;
    for (int i = 0; i < sh.length; i++) {
      int c = ((Num)a.arr[i]).intValue();
      sh[i] = c;
      ia*= c;
    }
    Value[] arr = new Value[ia];
    int index = 0;
    for (int i = 0; i < ia; i++) {
      arr[i] = w.arr[index++];
      if (index == w.ia) index = 0;
    }
    return new Arr(arr, sh);
  }
}
