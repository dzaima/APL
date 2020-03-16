package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.dimensions.*;
import APL.types.functions.Builtin;

public class FlipBuiltin extends Builtin implements DimMFn, DimDFn {
  @Override public String repr() {
    return "⊖";
  }
  
  
  @Override public Obj call(Value w, int dim) {
    return ((Arr) w).reverseOn(dim);
  }
  @Override public Obj call(Value w) {
    if (w instanceof Primitive) return w;
    return ((Arr) w).reverseOn(0);
  }
  @Override public Obj callInv(Value w) {
    return call(w);
  }
  
  @Override public Obj call(Value a, Value w) {
    if (a instanceof Primitive) return ReverseBuiltin.on(a.asInt(), 0, w);
    throw new DomainError("A⊖B not implemented for non-scalar A");
  }
  
  @Override public Obj call(Value a, Value w, int dim) {
    if (a instanceof Primitive) return ReverseBuiltin.on(a.asInt(), -dim-1, w);
    throw new DomainError("A⊖[n]B not implemented for non-scalar A");
  }
  
  @Override public Obj callInvW(Value a, Value w) {
    return call(numM(MinusBuiltin.NF, a), w);
  }
}