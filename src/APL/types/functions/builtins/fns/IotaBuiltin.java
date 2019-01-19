package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.*;

import static APL.Main.*;

public class IotaBuiltin extends Builtin {
  @Override public String repr() {
    return "⍳";
  }
  
  public IotaBuiltin(Scope sc) {
    super(sc);
  }
  public Obj call(Value w) {
    int IO = sc.IO;
    if (w instanceof Primitive) {
      double[] res = new double[w.asInt()];
      for (int i = 0; i < res.length; i++) res[i] = i + IO;
      return new DoubleArr(res);
    }
    int[] shape = w.asIntVec();
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
    if (w.ia > 20 && a.ia > 20) {
      HashMap<Value, Integer> map = new HashMap<>();
      int ctr = 0;
      for (Value v : a) {
        map.putIfAbsent(v, ctr);
        ctr++;
      }
      double[] res = new double[w.ia];
      ctr = 0;
      double notfound = IO + a.ia;
      for (Value v : w) {
        Integer f = map.get(v);
        res[ctr] = f==null? notfound : f + IO;
        ctr++;
      }
      return new DoubleArr(res, w.shape);
    }
    double[] res = new double[w.ia];
    int i = 0;
    for (Value wv : w) {
      int j = 0;
      for (Value av : a) {
        if (av.equals(wv)) break;
        j++;
      }
      res[i++] = j+IO;
    }
    return new DoubleArr(res, w.shape);
  }
}
