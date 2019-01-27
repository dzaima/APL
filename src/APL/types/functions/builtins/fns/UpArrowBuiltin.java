package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.RankError;
import APL.types.arrs.*;
import APL.types.functions.Builtin;
import APL.types.*;

import java.util.Arrays;

public class UpArrowBuiltin extends Builtin {
  @Override public String repr() {
    return "↑";
  }
  
  public UpArrowBuiltin(Scope sc) {
    super(sc);
  }
  public Obj call(Value a, Value w) { // TODO ⍴⍴⍺ < ⍴⍴⍵
    int IO = sc.IO;
    int[] shape = a.asIntVec();
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
    return Arr.create(arr, shape);
  }
  public Obj call(Value w) {
    if (w instanceof Arr) {
      Value[] subs = w.values();
      if (subs.length == 0) return w; // TODO prototypes
      
      int[] def = new int[subs[0].rank];
      System.arraycopy(subs[0].shape, 0, def, 0, def.length);
      for (Value v : subs) {
        if (v.rank != def.length) throw new RankError("expected equal ranks of items for ↑", v);
        for (int i = 0; i < def.length; i++) def[i] = Math.max(v.shape[i], def[i]);
      }
      int subIA = Arrays.stream(def).reduce(1, (a, b) -> a * b);
      int totalIA = subIA * Arrays.stream(w.shape).reduce(1, (a, b) -> a * b);
      int[] totalShape = new int[def.length + w.rank];
      System.arraycopy(w.shape, 0, totalShape, 0, w.rank);
      System.arraycopy(def, 0, totalShape, w.rank, def.length);
      
      boolean allNums = true;
      for (Value v : subs) {
        if (!v.quickDoubleArr()) {
          allNums = false;
          break;
        }
      }
      if (allNums) {
        double[] allVals = new double[totalIA];
  
        int i = 0;
        for (Value v : subs) {
          double[] c = v.asDoubleArr();
          int ia = c.length;
          int k = 0;
          for (int j : new SimpleIndexer(def, v.shape)) {
            allVals[i+j] = c[k++];
          }
          // automatic zero padding
          i+= subIA;
        }
  
        return new DoubleArr(allVals, totalShape);
      }
      Value[] allVals = new Value[totalIA];
  
      int i = 0;
      for (Value v : subs) {
        Value proto = v.prototype();
        for (int[] sh : new Indexer(def, 0)) {
//          System.out.println(v +" "+ Arrays.toString(sh) +" "+ v.at(sh, v.prototype) +" "+ Arrays.toString(v.shape));
          allVals[i++] = v.at(sh, proto);
        }
      }
      
      return Arr.create(allVals, totalShape);
    } else return w;
  }
}