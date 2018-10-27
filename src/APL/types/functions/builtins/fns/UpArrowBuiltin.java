package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.errors.RankError;
import APL.types.arrs.HArr;
import APL.types.functions.Builtin;
import APL.types.*;

import java.util.Arrays;

public class UpArrowBuiltin extends Builtin {
  public UpArrowBuiltin(Scope sc) {
    super("↑", 0x011, sc);
  }
  public Obj call(Value a, Value w) { // TODO ⍴⍴⍺ < ⍴⍴⍵
    int IO = sc.IO;
    int[] shape = a.asIntArr();
    if (shape.length == 0) return w;
    int ia = 1;
    int[] offsets = new int[shape.length];
    for (int i = 0; i < shape.length; i++) {
      int d = shape[i];
      ia *= d;
      if (d < 0) {
        shape[i] = -d;
        ia = -ia;
        offsets[i] = w.shape[i]-shape[i]+IO;
      } else offsets[i] = IO;
    }
    Value[] arr = new Value[ia];
    int i = 0;
    for (int[] index : new Indexer(shape, offsets)) {
      arr[i] = w.at(index, sc.IO);
      i++;
    }
    return new HArr(arr, shape);
  }
  public Obj call(Value w) {
    if (w instanceof Arr) {
      Arr arr = (Arr) w;
      Value[] sub = arr.values();
      if (sub.length == 0) return w; // TODO prototypes
      int[] def = new int[sub[0].rank];
      System.arraycopy(sub[0].shape, 0, def, 0, def.length);
      for (Value v : sub) {
        if (v.rank != def.length) throw new RankError("expected equal ranks of items for ↑", v);
        for (int i = 0; i < def.length; i++) def[i] = Math.max(v.shape[i], def[i]);
      }
      int totalIA = Arrays.stream(def).reduce(1, (a, b) -> a * b);
      totalIA *= Arrays.stream(arr.shape).reduce(1, (a, b) -> a * b);
      int[] totalShape = new int[def.length + arr.rank];
      System.arraycopy(arr.shape, 0, totalShape, 0, arr.rank);
      System.arraycopy(def, 0, totalShape, arr.rank, def.length);
      Value[] allVals = new Value[totalIA];
  
      int i = 0;
      for (Value v : sub) {
        for (int[] sh : new Indexer(def, 0)) {
//          System.out.println(v +" "+ Arrays.toString(sh) +" "+ v.at(sh, v.prototype) +" "+ Arrays.toString(v.shape));
          allVals[i++] = v.at(sh, v.prototype());
        }
      }
      
      return new HArr(allVals, totalShape);
    } else return w;
  }
}