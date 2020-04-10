package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.RankError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.HashMap;

import static APL.Main.toAPL;

public class IotaBuiltin extends Builtin {
  @Override public String repr() {
    return "⍳";
  }
  
  public IotaBuiltin(Scope sc) {
    super(sc);
  }
    
  public Value call(Value w) {
    int IO = sc.IO;
    if (w instanceof Primitive) {
      if (w instanceof Num) {
        double[] res = new double[w.asInt()];
        if (IO == 0) for (int i = 0; i < res.length; i++) res[i] = i;
        else for (int i = 0; i < res.length; i++) res[i] = i + 1;
        return new DoubleArr(res);
      } else if (w instanceof BigValue) {
        Value[] res = new Value[w.asInt()];
        for (int i = 0; i < res.length; i++) {
          res[i] = new BigValue(i+IO);
        }
        return new HArr(res);
      }
    }
    if (Main.vind) return new RhoBarBuiltin(sc).call(w);
    int[] shape = w.asIntVec();
    int ia = Arr.prod(shape);
    Value[] arr = new Value[ia];
    int i = 0;
    for (int[] c : new Indexer(shape, IO)) {
      arr[i] = toAPL(c);
      i++;
    }
    return new HArr(arr, shape);
  }
  
  public Value call(Value a, Value w) {
    return on(a, w, sc.IO);
  }
  
  public static Value on(Value a, Value w, int IO) {
    if (w.rank > 1) throw new RankError("⍵ for ⍳ had rank > 1", w);
    if (a.rank > 1) throw new RankError("⍺ for ⍳ had rank > 1", a);
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
      // w won't be a scalar
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
    if (w instanceof Primitive) return new Num(res[0]);
    if (w.rank == 0) return new Num(res[0]);
    return new DoubleArr(res, w.shape);
  }
}
