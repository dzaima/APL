package APL.types.functions.builtins.fns;

import APL.errors.RankError;
import APL.types.Value;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.*;

public class LShoeStileBuiltin extends Builtin {
  
  @Override public Value call(Value a, Value w) {
    HashMap<Value, Integer> counts = new HashMap<>();
    for (Value ca : a) counts.put(ca, 0);
    for (Value cw : w) {
      Integer pv = counts.get(cw);
      if (pv != null) counts.put(cw, pv + 1);
    }
    double[] res = new double[a.ia];
    int i = 0;
    for (Value ca : a) {
      res[i] = counts.get(ca);
      i++;
    }
    return DoubleArr.safe(res, a.shape);
  }
  
  @Override public Value call(Value w) {
    RankError.must(w.rank == 1, "rank of ⍵ must be 1");
    HashSet<Value> encountered = new HashSet<>();
    BitArr.BA res = new BitArr.BA(w.shape);
    for (Value cv : w) {
      if (encountered.contains(cv)) res.add(false);
      else {
        encountered.add(cv);
        res.add(true);
      }
    }
    return res.finish();
  }
  
  @Override public String repr() {
    return "⍧";
  }
}
