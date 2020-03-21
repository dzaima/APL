package APL.types.functions.builtins;

import APL.Type;
import APL.types.*;
import APL.types.functions.*;

public abstract class AbstractSet extends Fun {
  public abstract Obj callObj(Obj a, Obj w, boolean update);
  
  @Override
  public Type type() {
    return Type.set;
  }
}
