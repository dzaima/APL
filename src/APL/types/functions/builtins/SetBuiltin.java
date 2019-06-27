package APL.types.functions.builtins;

import APL.Type;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.*;

public class SetBuiltin extends AbstractSet {
  public static SetBuiltin inst = new SetBuiltin();
  
  @Override public String repr() {
    return "←";
  }
  
  
  
  @Override
  public Obj call(Value a, Value w) {
    return call(a, w, false);
  }
  
  public Obj call(Obj a, Obj w, boolean update) {
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

  public Obj call(Fun f, Obj a, Value w) {
    call(a, f.call((Value) ((Settable) a).get(), w), true);
    return w;
  }
}

