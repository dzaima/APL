package APL.types.functions.builtins.mops;

import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.*;

public class TableBuiltin extends Mop {
  @Override public String repr() {
    return "⌾";
  }
  
  
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    int[] shape = new int[a.rank+w.rank];
    System.arraycopy(a.shape, 0, shape, 0, a.rank);
    System.arraycopy(w.shape, 0, shape, a.rank, w.rank);
    
    if (a.ia==0 || w.ia==0) return new EmptyArr(shape, a.safePrototype());
    
    Fun ff = (Fun) f;
    
    
    int i = 0;
    Value first = ff.call(a.first(), w.first());
  
    if (first instanceof Num) {
      double[] dres = new double[a.ia * w.ia];
      boolean allNums = true;
      boolean firstSkipped = false;
      Value failure = null;
      
      numatt: for (Value na : a) {
        for (Value nw : w) {
          Value r;
          if (firstSkipped) r = ff.call(na, nw);
          else {
            firstSkipped = true;
            r = first;
          }
          if (r instanceof Num) dres[i++] = ((Num) r).num;
          else {
            allNums = false;
            failure = r;
            break numatt;
          }
        }
      }
      if (allNums) {
        if (shape.length == 0) return new Num(dres[0]);
        return new DoubleArr(dres, shape);
      } else { // i points to the place the failure should be
        Value[] res = new Value[a.ia*w.ia];
        for (int n = 0; n < i; n++) { // slowly copy the data back..
          res[n] = new Num(dres[n]);
        }
        res[i++] = failure; // insert that horrible thing that broke everything
        if (i%w.ia != 0) { // finish the damn row..
          Value va = a.get(i / w.ia);
          for (int wi = i % w.ia; wi < w.ia; wi++) {
            res[i++] = ff.call(va, w.get(wi));
          }
        }
        for (int ai = (i+w.ia-1)/w.ia; ai < a.ia; ai++) { // and do the rest, slowly and horribly
          Value va = a.get(ai);
          for (Value vw : w) {
            res[i++] = ff.call(va, vw);
          }
        }
        if (shape.length == 0 && res[0] instanceof Primitive) return res[0];
        return Arr.create(res, shape);
      }
    }
    boolean firstSkipped = false;
    Value[] arr = new Value[a.ia*w.ia];
    for (Value na : a) {
      for (Value nw : w) {
        if (firstSkipped) arr[i++] = ff.call(na, nw);
        else {
          firstSkipped = true;
          arr[i++] = first;
        }
      }
    }
    if (shape.length == 0 && arr[0] instanceof Primitive) return arr[0];
    return Arr.create(arr, shape);
  }
}