package APL.types.functions.builtins;

import APL.Type;
import APL.types.*;

public abstract class AbstractSet extends Callable {
  public AbstractSet() {
    super(null);
  }
  
  public abstract Obj callObj(Obj a, Obj w, boolean update);
  
  @Override
  public Type type() {
    return Type.set;
  }
}