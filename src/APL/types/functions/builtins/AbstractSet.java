package APL.types.functions.builtins;

import APL.Type;
import APL.types.*;
import APL.types.functions.Builtin;

public abstract class AbstractSet extends Builtin {
  public abstract Obj call(Obj a, Obj w, boolean update);
  
  @Override
  public Type type() {
    return Type.set;
  }
}
