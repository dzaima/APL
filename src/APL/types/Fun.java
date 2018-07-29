package APL.types;

import java.util.*;
import APL.*;
import APL.errors.*;

public abstract class Fun extends Obj {
  public Fun(Type t) {
    super(t);
  }
  public int valid;
  public Value identity = null;
  protected String htype() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 3; i++) {
      if (1 == (0xf & (valid >> 4*(2-i)))) {
        if (sb.length() > 0) sb.append("/");
        sb.append(new String[]{"niladic", "monadic", "dyadic"}[i]);
      }
    }
    return sb.toString();
  }
  public Obj call() {
    throw new IncorrectArgsException(htype() + " function "+toString()+" called niladically");
  }
  public Obj call(Value w) {
    throw new IncorrectArgsException(htype() + " function "+toString()+" called monadically with " + w);
  }
  public Obj call(Value a, Value w) {
    throw new IncorrectArgsException(htype() + " function "+toString()+" called dyadically with " + a + " and " + w);
  }

  protected Value vec(Value w) {
    if (!w.primitive()) {
      Arr o = ((Arr)w);
      Arr n = new Arr(o.shape);
      for (int i = 0; i < o.ia; i++) {
        n.arr[i] = vec(o.arr[i]);
      }
      return n;
    } else return scall(w);
  }
  protected Value vec(Value a, Value w) {

    if (a.primitive()) {
      if (w.primitive()) {
        return scall(a, w);
      } else {
        Arr ow = ((Arr)w);
        Arr n = new Arr(ow.shape);
        for (int i = 0; i < ow.ia; i++) {
          n.arr[i] = vec(a, ow.arr[i]);
        }
        return n;
      }
    } else {
      if (w.primitive()) {
        Arr oa = ((Arr)a);
        Arr n = new Arr(oa.shape);
        for (int i = 0; i < oa.ia; i++) {
          n.arr[i] = vec(oa.arr[i], w);
        }
        return n;
      } else {
        Arr oa = ((Arr)a);
        Arr ow = ((Arr)w);
        if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes not equal");
        Arr n = new Arr(oa.shape);
        for (int i = 0; i < oa.ia; i++) {
          n.arr[i] = vec(oa.arr[i], ow.arr[i]);
        }
        return n;
      }
    }
  }
  protected Value scall(Value w) { throw new IllegalStateException("scall not defined but called with " + w); }
  protected Value scall(Value a, Value w) { throw new IllegalStateException("scall not defined but called with " +a+ " and " +w); }
}
