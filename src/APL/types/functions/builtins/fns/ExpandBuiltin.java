package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

public class ExpandBuiltin extends Builtin {
  public String repr() {
    return "⍀";
  }
  
  public Value call(Value a, Value w) {
    RankError.must(a.rank==1, "⍺ of ⍀ bust be rank 1");
    if (w.rank >= 2) throw new NYIError("⍀: rank 2 or more ⍵", this, w);
    Value pr = null;
    int[] is = a.asIntArr(); // vectorness checked before
    int ram = 0;
    int iam = 0;
    for (int v : is) {
      ram+= Math.max(1, Math.abs(v));
      iam+= v>0? 1 : 0;
    }
    if (iam != w.ia) throw new DomainError("⍀: required input amount ("+iam+") not equal to given ("+w.ia+")");
    Value[] res = new Value[ram];
    int rp = 0;
    int ip = 0;
    
    for (int v : is) {
      if (v <= 0) {
        if (pr == null) pr = w.safePrototype();
        v = Math.max(1, -v);
        for (int i = 0; i < v; i++) res[rp++] = pr;
      } else {
        Value c = w.get(ip);
        for (int i = 0; i < v; i++) {
          res[rp++] = c;
        }
        ip++;
      }
    }
    
    return Arr.create(res);
  }
}