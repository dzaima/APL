package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

public class ReplicateBuiltin extends Builtin {
  public ReplicateBuiltin() {
    super("⌿");
    valid = 0x010;
  }
  
  public Obj call(Value a, Value w) {
    if (a.rank > 1) throw new RankError("⍺ for ⌿ should have rank ≤1", this, a);
    if (w.rank > 1) throw new RankError("⍵ for ⌿ should have rank ≤1", this, w);
    if (a.rank == 0) {
      int sz = a.toInt(this);
      Value[] res = new Value[w.ia*sz];
      int ptr = 0;
      for (int i = 0; i < w.ia; i++) {
        Value c = w.arr[i];
        for (int j = 0; j < sz; j++) {
          res[ptr++] = c;
        }
      }
      return new Arr(res);
    }
    if (a.ia != w.ia) throw new LengthError("⍺ & ⍵ should have equal lengths for ⌿");
    int total = 0;
    int[] sizes = new int[a.ia];
    int i = 0;
    for (Value v : a.arr) {
      int c = v.toInt(this);
      total+= c;
      sizes[i++] = c;
    }
    int ptr = 0;
    Value[] res = new Value[total];
    for (i = 0; i < a.ia; i++) {
      Value c = w.arr[i];
      for (int j = 0; j < sizes[i]; j++) {
        res[ptr++] = c;
      }
    }
    return new Arr(res);
  }
}