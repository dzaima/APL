package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class SquadBuiltin extends Builtin {
  @Override public String repr() {
    return "⌷";
  }
  
  public SquadBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    if (w instanceof Arr) return w;
    if (w instanceof APLMap) return ((APLMap) w).kvPair();
    throw new DomainError("⍵ not array nor map", this, w);
  }
  
  public Value call(Value a, Value w) {
    return a.at(w.asIntVec(), sc.IO);
  }
}