package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.HArr;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class ReplicateBuiltin extends Builtin {
  public ReplicateBuiltin() {
    super("⌿", 0x010);
  }
  
  public Obj call(Value a, Value w) {
    if (a.rank > 1) throw new RankError("⍺ for ⌿ should have rank ≤1", a);
    if (w.rank > 1) throw new RankError("⍵ for ⌿ should have rank ≤1", w);
    if (a.rank == 0) {
      int sz = a.asInt();
      if (sz < 0) {
        Value[] res = new Value[w.ia*-sz];
        Value n = w.first() instanceof Char? Char.SPACE : Num.ZERO;
        Arrays.fill(res, n);
        return new HArr(res);
      }
      Value[] res = new Value[w.ia*sz];
      int ptr = 0;
      for (int i = 0; i < w.ia; i++) {
        Value c = w.get(i);
        for (int j = 0; j < sz; j++) {
          res[ptr++] = c;
        }
      }
      return new HArr(res);
    }
    if (a.ia != w.ia) throw new LengthError("⍺ & ⍵ should have equal lengths for ⌿", w);
    int total = 0;
    int[] sizes = a.asIntVec();
    for (int i = 0; i < a.ia; i++) {
      total+= Math.abs(sizes[i]);
    }
    int ptr = 0;
    Value[] res = new Value[total];
    for (int i = 0; i < a.ia; i++) {
      Value c = w.get(i);
      int am = sizes[i];
      if (sizes[i] < 0) {
        am = -am;
        c = c.prototype();
      }
      for (int j = 0; j < am; j++) {
        res[ptr++] = c;
      }
    }
    return new HArr(res);
  }
}