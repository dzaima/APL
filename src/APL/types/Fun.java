package APL.types;

import java.util.*;
import java.util.stream.*;

import APL.*;
import APL.errors.*;

@SuppressWarnings({"unused", "Convert2streamapi"}) // class for getting overridden & being fast so no streams
public abstract class Fun extends Obj {
  public Scope sc;
  public int valid; // 0x niladic dyadic monadic
  
  public Value identity = null;
  protected String htype() {
    return IntStream.range(0, 3)
      .filter(i -> 1 == (0xf & (valid >> 4 * (2-i))))
      .mapToObj(i -> new String[]{"niladic", "dyadic", "monadic"}[i])
      .collect(Collectors.joining("/"));
  }
  public Obj call() {
    throw new IncorrectArgsException(htype() + " function "+toString()+" called niladically", this);
  }
  public Obj call(Value w) {
    throw new IncorrectArgsException(htype() + " function "+toString()+" called monadically", this, w);
  }
  public Obj call(Value a, Value w) {
    throw new IncorrectArgsException(htype() + " function "+toString()+" called dyadically", this, a);
  }
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
  public Value scalar (VecFun f, Value w) {
    if (w.primitive()) {
      return f.call(w);
    } else {
      Arr o = ((Arr)w);
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = scalar(f, o.arr[i]);
      }
      return new Arr(arr, o.shape);
    }
  }
  
  
  public interface NumVecFun {
    Value call(Num w);
  }
  public interface ChrVecFun {
    Value call(Char w);
  }
  public interface MapVecFun {
    Value call(APLMap w);
  }
  protected Value numChr(NumVecFun nf, ChrVecFun cf, Value w) {
    if (!w.primitive()) {
      Arr o = ((Arr)w);
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numChr(nf, cf, o.arr[i]);
      }
      return new Arr(arr, o.shape);
    } else if (w instanceof Char) return cf.call((Char)w);
    else if (w instanceof Num) return nf.call((Num) w);
    else throw new DomainError("Expected either number or character argument, got "+w.humanType(false), null, w);
  }
  protected Value num(NumVecFun nf, Value w) {
    if (!w.primitive()) {
      Arr o = ((Arr)w);
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = num(nf, o.arr[i]);
      }
      return new Arr(arr, o.shape);
    } else if (w instanceof Num) return nf.call((Num) w);
    else throw new DomainError("Expected number, got "+w.humanType(false), null, w);
  }
  protected Value numChrMap(NumVecFun nf, ChrVecFun cf, MapVecFun mf, Value w) {
    if (!w.primitive()) {
      Arr o = ((Arr)w);
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numChr(nf, cf, o.arr[i]);
      }
      return new Arr(arr, o.shape);
    } else if (w instanceof Char) return cf.call((Char)w);
    else if (w instanceof Num) return nf.call((Num) w);
    else if (w instanceof APLMap) return mf.call((APLMap) w);
    else throw new DomainError("Expected either number/char/map, got "+w.humanType(false), null, w);
  }
  
  public interface DyVecFun {
    Value call(Value a, Value w);
  }
  protected Value scalar(DyVecFun f, Value a, Value w) {
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
          arr[i] = scalar(f, oa.arr[i], w);
        }
        return new Arr(arr, oa.shape);
      } else {
        Arr oa = ((Arr)a);
        Arr ow = ((Arr)w);
        if (oa.rank != ow.rank) throw new LengthError("ranks don't equal (shapes: "+ Main.formatAPL(oa.shape)+" vs "+ Main.formatAPL(ow.shape) +")", this, w);
        if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes don't match ("+ Main.formatAPL(oa.shape)+" vs "+ Main.formatAPL(ow.shape) +")", this, w);
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = scalar(f, oa.arr[i], ow.arr[i]);
        }
        return new Arr(arr, oa.shape);
      }
    }
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
}
