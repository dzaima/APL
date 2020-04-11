package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

public class LShoeBuiltin extends Builtin {
  @Override public String repr() {
    return "⊂";
  }
  
  public LShoeBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    if (w instanceof Primitive) return w;
    else if (w.ia == 0) throw new DomainError("⊂ on array with 0 elements", this, w);
    else return w.first();
  }
  
  
  public Value call(Value a, Value w) {
    Obj o = callObj(a, w);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Value a, Value w) {
    if (a instanceof APLMap) {
      APLMap map = (APLMap) a;
      return map.getRaw(w);
    }
    if (w instanceof Num) {
      if (a.rank != 1) throw new RankError("array rank was "+a.rank+", tried to get item at rank 0", a);
      if (a.ia == 0) throw new LengthError("⊂ on array with 0 elements", this, a);
      int p = w.asInt() - sc.IO;
      if (p >= a.ia) throw new DomainError("Tried to access item at position "+w+" while shape was "+(a.ia-1));
      return a.get(p);
    }
    for (Value v : w) {
      a = a.at(v.asIntVec(), sc.IO);
    }
    return a;
  }
}