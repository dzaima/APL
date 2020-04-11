package APL.types.functions.builtins;

import APL.Type;
import APL.types.Obj;

public abstract class AbstractSet extends Obj {
  public abstract Obj callObj(Obj a, Obj w, boolean update);
  
  @Override
  public Type type() {
    return Type.set;
  }
}