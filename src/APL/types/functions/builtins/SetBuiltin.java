package APL.types.functions.builtins;

import APL.Type;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.*;

import java.util.*;

public class SetBuiltin extends Builtin {
  public SetBuiltin() {
    super("←");
    valid = 0x010;
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
//      if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes not equal"); TODO shapes
      if (ow.rank != 1) throw new LengthError("← scatter rank ≠1", this, ow);
      if (ow.ia != oa.ia) throw new LengthError("← scatter argument lengths not equal", this, ow);
//      Value[] arr = new Value[oa.ia];
      for (int i = 0; i < oa.ia; i++) {
//        arr[i] = (Value)
        this.call(oa.arr.get(i), ow.arr[i], update);
      }
      return w; //new Arr(arr, oa.shape);
    } else {
//      Value[] arr = new Value[oa.ia];
      for (int i = 0; i < oa.ia; i++) {
//        arr[i] = (Value)
        this.call(oa.arr.get(i), w, update);
      }
      return w; //new Arr(arr, oa.shape);
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

