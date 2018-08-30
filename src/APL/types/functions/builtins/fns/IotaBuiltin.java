package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;

import static APL.Main.*;

public class IotaBuiltin extends Builtin {
  public IotaBuiltin(Scope sc) {
    super("⍳");
    this.sc = sc;
    valid = 0x011;
  }
  public Obj call(Value w) {
    if (w.primitive()) {
      Value[] is = w.arr;
      Value[] res = new Value[((Num) is[0]).intValue()];
      for (int i = 0; i < res.length; i++) res[i] = new Num(i + ((Num) sc.get("⎕IO")).intValue());
      return new Arr(res);
    }
    int[] shape = w.toIntArr(this);
    int ia = 1;
    for (int i : shape) ia*= i;
    Value[] arr = new Value[ia];
    int i = 0;
    for (int[] c : new Indexer(shape, ((Num) sc.get("⎕IO")).toInt(this))) {
      arr[i] = toAPL(c);
      i++;
    }
    return new Arr(arr, shape);
  }
}
