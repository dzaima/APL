package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.RankError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class UpArrowBuiltin extends Builtin {
  @Override public String repr() {
    return "↑";
  }
  
  public UpArrowBuiltin(Scope sc) {
    super(sc);
  }
  public Value call(Value a, Value w) { // TODO ⍴⍺ < ⍴⍴⍵
    int IO = sc.IO;
    int[] shape = w.asIntVec();
    if (shape.length == 0) return a;
    int ia = 1;
    int[] offsets = new int[shape.length];
    for (int i = 0; i < shape.length; i++) {
      int d = shape[i];
      ia *= d;
      if (d < 0) {
        shape[i] = -d;
        ia = -ia;
        offsets[i] = a.shape[i]-shape[i]+IO;
      } else offsets[i] = IO;
    }
    Value[] arr = new Value[ia];
    int i = 0;
    for (int[] index : new Indexer(shape, offsets)) {
      arr[i] = a.at(index, sc.IO);
      i++;
    }
    return Arr.create(arr, shape);
  }
  public Value call(Value w) {
    if (w instanceof Arr) {
      if (w instanceof DoubleArr || w instanceof ChrArr || w instanceof BitArr) return w;
      Value[] subs = w.values();
      if (subs.length == 0) return w;
      
      int[] def = new int[subs[0].rank];
      System.arraycopy(subs[0].shape, 0, def, 0, def.length);
      for (Value v : subs) {
        if (v.rank != def.length) throw new RankError("expected equal ranks of items for ↑", v);
        for (int i = 0; i < def.length; i++) def[i] = Math.max(v.shape[i], def[i]);
      }
      int subIA = Arr.prod(def);
      int totalIA = subIA * Arr.prod(w.shape);
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
          // System.out.println(v +" "+ Arrays.toString(sh) +" "+ v.at(sh, v.prototype) +" "+ Arrays.toString(v.shape));
          allVals[i++] = v.at(sh, proto);
        }
      }
      
      return Arr.create(allVals, totalShape);
    } else return w;
  }
  
  public static Value merge(Value[] w) {
    int[] def = new int[w[0].rank];
    System.arraycopy(w[0].shape, 0, def, 0, def.length);
    for (Value v : w) {
      if (v.rank != def.length) throw new RankError("expected equal ranks of items for ↑", v);
      for (int i = 0; i < def.length; i++) def[i] = Math.max(v.shape[i], def[i]);
    }
    int subIA = Arr.prod(def);
    int totalIA = subIA * w.length;
    int[] totalShape = new int[def.length + 1];
    totalShape[0] = w.length;
    System.arraycopy(def, 0, totalShape, 1, def.length);
    
    boolean allNums = true;
    for (Value v : w) {
      if (!v.quickDoubleArr()) {
        allNums = false;
        break;
      }
    }
    if (allNums) {
      double[] allVals = new double[totalIA];
      
      int i = 0;
      for (Value v : w) {
        double[] c = v.asDoubleArr();
        int k = 0;
        for (int j : new SimpleIndexer(def, v.shape)) {
          allVals[i+j] = c[k++];
        }
        // automatic zero padding
        i+= subIA;
      }
      
      return new DoubleArr(allVals, totalShape);
    } else {
      Value[] allVals = new Value[totalIA];
      
      int i = 0;
      for (Value v : w) {
        Value proto = v.prototype();
        for (int[] sh : new Indexer(def, 0)) {
          // System.out.println(v +" "+ Arrays.toString(sh) +" "+ v.at(sh, v.prototype) +" "+ Arrays.toString(v.shape));
          allVals[i++] = v.at(sh, proto);
        }
      }
      return Arr.create(allVals, totalShape);
    }
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(a, w)) : (Value) o;
    return undo(a.asIntVec(), v, w);
  }
  public static Value undo(int[] e, Value w, Value origW) {
    Value[] r = new Value[origW.ia];
    int[] s = origW.shape;
    Indexer idx = new Indexer(s, 0);
    int[] tmp = new int[e.length];
    for (int[] i : idx) {
      Value c;
      boolean in = true;
      for (int j = 0; j < e.length; j++) {
        int ep = e[j];
        int ip = i[j];
        int lp = s[j];
        if (ep<0? ip <= lp+ep-1 : ip >= ep) {
          in = false;
          break;
        }
      }
      if (in) {
        for (int j = 0; j < e.length; j++) {
          tmp[j] = e[j]<0? i[j]-e[j]-s[j]: i[j];
        }
        c = w.simpleAt(tmp);
      } else {
        c = origW.simpleAt(i);
      }
      r[idx.pos()] = c;
      
    }
    
    return Arr.create(r, s);
  }
}