package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class RShoeBuiltin extends Builtin {
  @Override public String repr() {
    return "⊃";
  }
  
  public RShoeBuiltin(Scope sc) {
    super(sc);
  }
  
  public Obj call(Value w) {
    if (w instanceof Primitive) return w;
    else if (w.ia == 0) throw new DomainError("⊃ on array with 0 elements", w);
    else return w.first();
  }
  
  public Obj call(Value a, Value w) {
    if (w instanceof APLMap) {
      APLMap map = (APLMap) w;
      return map.getRaw(a);
    }
    for (Value v : a) {
      w = w.at(v.asIntVec(), sc.IO);
    }
    return w;
  }
}