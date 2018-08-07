package APL.types;

import java.util.*;
import APL.*;
import APL.errors.*;

public abstract class Fun extends Obj {
  public Scope sc;
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
        sb.append(new String[]{"niladic", "dyadic", "monadic"}[i]);
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
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = vec(o.arr[i]);
      }
      return new Arr(arr, o.shape);
    } else return scall(w);
  }
  protected Value vec(Value a, Value w) {

    if (a.primitive()) {
      if (w.primitive()) {
        return scall(a, w);
      } else {
        Arr ow = ((Arr)w);
        Value[] arr = new Value[ow.ia];
        for (int i = 0; i < ow.ia; i++) {
          arr[i] = vec(a, ow.arr[i]);
        }
        return new Arr(arr, ow.shape);
      }
    } else {
      if (w.primitive()) {
        Arr oa = ((Arr)a);
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = vec(oa.arr[i], w);
        }
        return new Arr(arr, oa.shape);
      } else {
        Arr oa = ((Arr)a);
        Arr ow = ((Arr)w);
        if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes not equal");
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = vec(oa.arr[i], ow.arr[i]);
        }
        return new Arr(arr, oa.shape);
      }
    }
  }
  protected Value scall(Value w) { throw new IllegalStateException("scall not defined but called with " + w); }
  protected Value scall(Value a, Value w) { throw new IllegalStateException("scall not defined but called with " +a+ " and " +w); }
}
