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
  @SuppressWarnings("unused")
  public Obj callInv(Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", this, w);
  }
  public Obj callInvW(Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", this, w);
  }
  public Obj callInvA(Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍺", this, w);
  }
  
  
  public interface VecFun {
    Value call(Value w);
  }
  public static Value scalar (VecFun f, Value w) {
    if (!w.primitive()) {
      Arr o = ((Arr)w);
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = scalar(f, o.arr[i]);
      }
      return new Arr(arr, o.shape);
    } else return f.call(w);
  }
  
  
  public interface NumVecFun {
    Value call(Num w);
  }
  public interface ChrVecFun {
    Value call(Char w);
  }
  protected static Value numChr(NumVecFun nf, ChrVecFun cf, Value w) {
    if (!w.primitive()) {
      Arr o = ((Arr)w);
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numChr(nf, cf, o.arr[i]);
      }
      return new Arr(arr, o.shape);
    } else if (w instanceof Char) return cf.call((Char)w);
      else if (w instanceof  Num) return nf.call((Num) w);
      else throw new DomainError("Expected either number or character argument, got "+Main.human(w.valtype, false), null, w);
  }
  
  public interface DyVecFun {
    Value call(Value a, Value w);
  }
  protected static Value scalar(DyVecFun f, Value a, Value w) {
    if (a.primitive()) {
      if (w.primitive()) {
        return f.call(a, w);
      } else {
        Arr ow = ((Arr)w);
        Value[] arr = new Value[ow.ia];
        for (int i = 0; i < ow.ia; i++) {
          arr[i] = scalar(f, a, ow.arr[i]);
        }
        return new Arr(arr, ow.shape);
      }
    } else {
      if (w.primitive()) {
        Arr oa = ((Arr)a);
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = f.call(oa.arr[i], w);
        }
        return new Arr(arr, oa.shape);
      } else {
        Arr oa = ((Arr)a);
        Arr ow = ((Arr)w);
        if (oa.rank != ow.rank) throw new LengthError("ranks don't equal (shapes: "+ Main.formatAPL(oa.shape)+" vs "+ Main.formatAPL(ow.shape) +")");
        if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes don't match ("+ Main.formatAPL(oa.shape)+" vs "+ Main.formatAPL(ow.shape) +")");
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = scalar(f, oa.arr[i], ow.arr[i]);
        }
        return new Arr(arr, oa.shape);
      }
    }
  }
}
