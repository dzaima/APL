package APL.types.functions.builtins;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;

public class SetBuiltin extends Builtin {
  public SetBuiltin() {
    super("←");
    valid = 0x010;
  }

  public Obj call(Obj a, Obj w) {
    if (a.setter) {
      Obj res = a.set(w);
      res.shy = true;
      return res;
    }
    if (w instanceof Arr) {
      Arr oa = (Arr) a;
      Arr ow = (Arr) w;
      if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes not equal");
      Value[] arr = new Value[oa.ia];
      for (int i = 0; i < oa.ia; i++) {
        arr[i] = (Value) this.call(oa.arr[i], (Obj) ow.arr[i]);
      }
      return new Arr(arr, oa.shape);
    } else {
      Arr oa = ((Arr) a);
      Value[] arr = new Value[oa.ia];
      for (int i = 0; i < oa.ia; i++) {
        arr[i] = (Value) this.call(oa.arr[i], w);
      }
      return new Arr(arr, oa.shape);
    }
  }

  public Obj call(Fun f, Value a, Value b) {
    return call(a, f.call(a, b));
  }
}

