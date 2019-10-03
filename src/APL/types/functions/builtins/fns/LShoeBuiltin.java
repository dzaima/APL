package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class LShoeBuiltin extends Builtin {
  @Override public String repr() {
    return "⊂";
  }
  
  public LShoeBuiltin(Scope sc) {
    super(sc);
  }
  
  public Obj call(Value w) {
    if (w instanceof Primitive) return w;
    else if (w.ia == 0) throw new DomainError("⊂ on array with 0 elements", this, w);
    else return w.first();
  }
  
  public Obj call(Value a, Value w) {
    if (a instanceof APLMap) {
      APLMap map = (APLMap) a;
      return map.getRaw(w);
    }
    for (Value v : w) {
      a = a.at(v.asIntVec(), sc.IO);
    }
    return a;
  }
}
