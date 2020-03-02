package APL.types.functions.builtins.dops;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.*;
import APL.types.functions.builtins.fns.DepthBuiltin;

public class OverBuiltin extends Dop {
  @Override public String repr() {
    return "⍥";
  }
  
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    isFn(aa, '⍶');
    int d = ((Value) ww).asInt();
    return on(derv, (Fun) aa, d, w);
  }
  public static Value on(Fun caller, Fun f, int d, Value w) {
    int ld = DepthBuiltin.lazy(w);
    if (ld==d || ld <= -d) {
      int fd = DepthBuiltin.full(w);
      if (d>0 && d!=fd) throw new DomainError(caller+" can't match a depth " + fd + " array", caller, w);
      if (d <= fd) {
        return (Value) f.call(w);
      }
    }
    if (d>0 && ld < d) throw new DomainError(caller+" can't match a depth "+DepthBuiltin.full(w)+" array", caller, w);
    Value[] res = new Value[w.ia];
    for (int i = 0; i < res.length; i++) {
      res[i] = on(caller, f, d, w.get(i));
    }
    return Arr.createL(res, w.shape);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶'); isFn(ww, '⍹');
    var WW = (Fun) ww;
    return ((Fun)aa).call((Value) WW.call(a), (Value) WW.call(w));
  }
}