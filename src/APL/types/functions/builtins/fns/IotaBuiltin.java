package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

import static APL.Main.*;

public class IotaBuiltin extends Builtin {
  public IotaBuiltin(Scope sc) {
    super("⍳");
    this.sc = sc;
    valid = 0x011;
  }
  public Obj call(Value w) {
    int IO = ((Num) sc.get("⎕IO")).intValue();
    if (w.primitive()) {
      Value[] is = w.arr;
      Value[] res = new Value[((Num) is[0]).intValue()];
      for (int i = 0; i < res.length; i++) res[i] = new Num(i + IO);
      return new Arr(res);
    }
    int[] shape = w.toIntArr(this);
    int ia = Arrays.stream(shape).reduce(1, (a, b) -> a * b);
    Value[] arr = new Value[ia];
    int i = 0;
    for (int[] c : new Indexer(shape, IO)) {
      arr[i] = toAPL(c);
      i++;
    }
    return new Arr(arr, shape);
  }
  
  @Override
  public Obj call(Value a, Value w) {
    if (w.rank > 1) throw new RankError("⍵ for ⍳ had rank > 1", this, w);
    if (a.rank > 1) throw new RankError("⍺ for ⍳ had rank > 1", this, a);
    int IO = ((Num) sc.get("⎕IO")).intValue();
    Value[] res = new Value[w.ia];
    for (int i = 0; i < w.ia; i++) {
      int j = 0;
      for (var c = w.arr[i]; j < a.ia; j++) {
        if (a.arr[j].equals(c)) break;
      }
      res[i] = new Num(j+IO);
    }
    return new Arr(res);
  }
}
