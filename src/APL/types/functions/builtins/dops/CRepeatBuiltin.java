package APL.types.functions.builtins.dops;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

import java.util.ArrayList;

public class CRepeatBuiltin extends Dop {
  @Override public String repr() {
    return "⍡";
  }
  
  public CRepeatBuiltin(Scope sc) {
    super(sc);
  }
  
  @Override public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶');
    if (ww instanceof Fun) {
      ArrayList<Value> res = new ArrayList<>();
      Value prev = w;
      res.add(prev);
      
      Value next = aaf.call(prev);
      res.add(next);
      Fun wwf = (Fun) ww;
      while(!Main.bool(wwf.call(prev, next))) {
        prev = next;
        next = aaf.call(prev);
        res.add(next);
      }
      return Arr.create(res);
    } else {
      int n = ((Value) ww).asInt();
      Value[] res = new Value[n];
      Value curr = w;
      for (int i = 0; i < n; i++) {
        curr = aaf.call(curr);
        res[i] = curr;
      }
      return Arr.create(res);
    }
  }
}
