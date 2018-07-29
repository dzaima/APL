package APL.types.functions.builtins;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;

public class SetBuiltin extends Builtin {
  public SetBuiltin() {
    super("←");
    valid = 0x001;
  }

  public Obj call(Value a, Obj w) {
    if (a.setter) {
      Obj res = a.set(w);
      res.shy = true;
      return res;
    }
    if (w instanceof Arr) {
      Arr oa = (Arr) a;
      Arr ow = (Arr) w;
      if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes not equal");
      Arr n = new Arr(oa.shape);
      for (int i = 0; i < oa.ia; i++) {
        n.arr[i] = (Value) this.call(oa.arr[i], (Obj) ow.arr[i]);
      }
      return n;
    } else {
      Arr oa = ((Arr) a);
      Arr n = new Arr(oa.shape);
      for (int i = 0; i < oa.ia; i++) {
        n.arr[i] = (Value) this.call(oa.arr[i], w);
      }
      return n;
    }
  }

  public Obj call(Fun f, Value a, Value b) {
    return call(a, f.call(a, b));
  }
}

