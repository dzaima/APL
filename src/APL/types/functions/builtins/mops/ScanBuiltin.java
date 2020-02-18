package APL.types.functions.builtins.mops;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.*;

public class ScanBuiltin extends Mop {
  @Override public String repr() {
    return "\\";
  }
  
  public Obj call(Obj aa, Value w, DerivedMop derv) {
    isFn(aa);
    // TODO ranks
    Fun f = (Fun) aa;
    if (w.ia == 0) return w;
    Value c = w.get(0);
    Value[] res = new Value[w.ia];
    res[0] = c;
    for (int i = 1; i<w.ia; i++) {
      c = (Value) f.call(c, w.get(i));
      res[i] = c;
    }
    return Arr.create(res);
  }
  
  public Obj call(Obj aa, Value a, Value w, DerivedMop derv) {
    isFn(aa);
    int n = a.asInt();
    int len = w.ia;
    DomainError.must(n>=0, "⍺ of \\ should be non-negative (was "+n+")");
    RankError.must(w.rank <= 1, "rank of ⍵ of \\ should be less than 2 (was "+w.rank+")");
    
    if (w.quickDoubleArr()) {
      Value[] res = new Value[len-n+1];
      double[] wa = w.asDoubleArr();
      for (int i = 0; i < res.length; i++) {
        double[] curr = new double[n];
        System.arraycopy(wa, i, curr, 0, n);
        res[i] = (Value) ((Fun) aa).call(new DoubleArr(curr));
      }
      return Arr.create(res);
    }
    
    Value[] res = new Value[len-n+1];
    Value[] wa = w.values();
    for (int i = 0; i < res.length; i++) {
      Value[] curr = new Value[n];
      // for (int j = 0; j < n; j++) curr[j] = wa[i + j];
      System.arraycopy(wa, i, curr, 0, n);
      res[i] = (Value) ((Fun) aa).call(Arr.create(curr));
    }
    return Arr.create(res);
  }
}