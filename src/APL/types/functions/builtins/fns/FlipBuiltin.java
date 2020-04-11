package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.dimensions.*;
import APL.types.functions.Builtin;

public class FlipBuiltin extends Builtin implements DimMFn, DimDFn {
  @Override public String repr() {
    return "⊖";
  }
  
  
  @Override public Value call(Value w, int dim) {
    return ((Arr) w).reverseOn(dim);
  }
  @Override public Value call(Value w) {
    if (w instanceof Primitive) return w;
    return ((Arr) w).reverseOn(0);
  }
  @Override public Value callInv(Value w) {
    return call(w);
  }
  
  @Override public Value call(Value a, Value w) {
    if (w instanceof Primitive) return ReverseBuiltin.on(w.asInt(), 0, a);
    throw new DomainError("A⊖B not implemented for non-scalar B");
  }
  
  @Override public Value call(Value a, Value w, int dim) {
    if (w instanceof Primitive) return ReverseBuiltin.on(w.asInt(), -dim-1, a);
    throw new DomainError("A⊖[n]B not implemented for non-scalar B");
  }
  
  @Override public Value callInvW(Value a, Value w) {
    return call(numM(MinusBuiltin.NF, a), w);
  }
}