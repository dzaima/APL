package APL.types.functions.builtins;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.*;

public class SetBuiltin extends AbstractSet {
  public static SetBuiltin inst = new SetBuiltin();
  
  @Override public String repr() {
    return "←";
  }
  
  
  
  public Value call(Value a, Value w) {
    Obj o = callObj(a, w);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Value a, Value w) {
    return callObj(a, w, false);
  }
  
  public Obj callObj(Obj a, Obj w, boolean update) {
    if (!(a instanceof Settable)) throw new SyntaxError(a + " isn't settable", a);
    Settable as = (Settable) a;
    if (update) {
      if (a instanceof Variable) ((Variable) a).update(w);
      else if (a instanceof VarArr) ((VarArr) a).set(w, true);
      else as.set(w); // throw new SyntaxError("can't set", a); todo?
    } else {
      as.set(w);
    }
    return w;
  }

  public Obj callObj(Fun f, Obj a, Value w) {
    callObj(a, f.call((Value) ((Settable) a).get(), w), true);
    return w;
  }
}

