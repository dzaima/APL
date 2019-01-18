package APL.types.functions.builtins;

import APL.Type;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.*;

public class SetBuiltin extends Builtin {
  public SetBuiltin() {
    super("←");
  }
  
  @Override
  public Obj call(Value a, Value w) {
    return call(a, w, false);
  }
  
  public Obj call(Obj a, Obj w, boolean update) {
    if (a instanceof Settable) {
      if (update) {
        ((Variable) a).update(w);
      } else {
        ((Settable) a).set(w);
      }
      return w;
    }
    VarArr oa = (VarArr) a;
    if (w instanceof Arr) {
      Arr ow = (Arr) w;
      if (ow.rank != 1) throw new LengthError("← scatter rank ≠1", ow);
      if (ow.ia != oa.ia) throw new LengthError("← scatter argument lengths not equal", ow);
      for (int i = 0; i < oa.ia; i++) {
        this.call(oa.arr.get(i), ow.get(i), update);
      }
      return w;
    } else {
      for (int i = 0; i < oa.ia; i++) {
        this.call(oa.arr.get(i), w, update);
      }
      return w;
    }
  }

  public Obj call(Fun f, Obj a, Value b) {
    return call(a, f.call((Value) ((Variable) a).get(), b), true);
  }
  
  @Override
  public Type type() {
    return Type.set;
  }
}

