package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

import static APL.Main.*;

public class IotaBuiltin extends Builtin {
  public IotaBuiltin(Scope sc) {
    super("⍳", 0x011, sc);
  }
  public Obj call(Value w) {
    int IO = sc.IO;
    if (w.primitive()) {
      double[] res = new double[w.asInt()];
      for (int i = 0; i < res.length; i++) res[i] = i + IO;
      return new DoubleArr(res);
    }
    int[] shape = w.asIntArr();
    int ia = Arrays.stream(shape).reduce(1, (a, b) -> a * b);
    Value[] arr = new Value[ia];
    int i = 0;
    for (int[] c : new Indexer(shape, IO)) {
      arr[i] = toAPL(c);
      i++;
    }
    return new HArr(arr, shape);
  }
  
  @Override
  public Obj call(Value a, Value w) {
    if (w.rank > 1) throw new RankError("⍵ for ⍳ had rank > 1", w);
    if (a.rank > 1) throw new RankError("⍺ for ⍳ had rank > 1", a);
    int IO = sc.IO;
    Value[] res = new Value[w.ia];
    for (int i = 0; i < w.ia; i++) {
      int j = 0;
      for (var c = w.get(i); j < a.ia; j++) {
        if (a.get(j).equals(c)) break;
      }
      res[i] = new Num(j+IO);
    }
    return new HArr(res);
  }
}
