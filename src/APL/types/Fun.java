package APL.types;

import APL.*;
import APL.errors.*;
import APL.types.arrs.*;

import java.util.Arrays;

@SuppressWarnings({"unused", "Convert2streamapi"}) // class for getting overridden & being fast so no streams
public abstract class Fun extends Scopeable {
  public final int valid; // 0x niladic dyadic monadic
  
  public Value identity = null;
  
  protected Fun(int valid, Scope sc) {
    super(sc);
    this.valid = valid;
  }
  protected Fun(int valid) {
    super(null);
    this.valid = valid;
  }
  public Obj call() {
    throw new IncorrectArgsException("function "+toString()+" called niladically", this);
  }
  public Obj call(Value w) {
    throw new IncorrectArgsException("function "+toString()+" called monadically", w);
  }
  public Obj call(Value a, Value w) {
    throw new IncorrectArgsException("function "+toString()+" called dyadically", a);
  }
  public Obj callInv(Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", w);
  }
  public Obj callInvW(Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", w);
  }
  public Obj callInvA(Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍺", w);
  }
  
  
  public interface VecFun {
    Value call(Value w);
  }
  public Value scalar (VecFun f, Value w) {
    if (w.primitive()) {
      return f.call(w);
    } else {
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = scalar(f, o.get(i));
      }
      return new HArr(arr, o.shape);
    }
  }
  
  
  public interface NumVecFun {
    Value call(Num w);
    default Arr call(DoubleArr a) {
      Value[] res = new Value[a.ia];
      for (int i = 0; i < a.ia; i++) res[i] = call(new Num(a.arr[i]));
      return new HArr(res, a.shape);
    }
  }
  public interface ChrVecFun {
    Value call(Char w);
    default Arr call(ChrArr a) {
      Value[] res = new Value[a.ia];
      for (int i = 0; i < a.ia; i++) res[i] = call(new Char(a.s.charAt(i)));
      return new HArr(res, a.shape);
    }
  }
  public interface MapVecFun {
    Value call(APLMap w);
  }
  protected Value numChr(NumVecFun nf, ChrVecFun cf, Value w) {
    if (w instanceof Arr) {
      if (w instanceof DoubleArr) return nf.call((DoubleArr) w);
      if (w instanceof    ChrArr) return cf.call((ChrArr   ) w);
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numChr(nf, cf, o.get(i));
      }
      return new HArr(arr, o.shape);
    } else if (w instanceof Char) return cf.call((Char)w);
    else if (w instanceof Num) return nf.call((Num) w);
    else throw new DomainError("Expected either number or character argument, got "+w.humanType(false), w);
  }
  protected Value num(NumVecFun nf, Value w) {
    if (w instanceof Arr) {
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = num(nf, o.get(i));
      }
      return new HArr(arr, o.shape);
    } else if (w instanceof Num) return nf.call((Num) w);
    else throw new DomainError("Expected number, got "+w.humanType(false), w);
  }
  protected Value numChrMap(NumVecFun nf, ChrVecFun cf, MapVecFun mf, Value w) {
    if (w instanceof Arr) {
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numChr(nf, cf, o.get(i));
      }
      return new HArr(arr, o.shape);
    } else if (w instanceof Char  ) return cf.call((Char  ) w);
      else if (w instanceof Num   ) return nf.call((Num   ) w);
      else if (w instanceof APLMap) return mf.call((APLMap) w);
    else throw new DomainError("Expected either number/char/map, got "+w.humanType(false), w);
  }
  
  public interface DyVecFun {
    Value call(Value a, Value w);
  }
  protected Value scalar(DyVecFun f, Value a, Value w) {
    if (a instanceof Primitive) {
      if (w instanceof Primitive) {
        return f.call(a, w);
      } else {
        Arr ow = (Arr) w;
        Value[] arr = new Value[ow.ia];
        for (int i = 0; i < ow.ia; i++) {
          arr[i] = scalar(f, a, ow.get(i));
        }
        return new HArr(arr, ow.shape);
      }
    } else {
      if (w instanceof Primitive) {
        Arr oa = (Arr) a;
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = scalar(f, oa.get(i), w);
        }
        return new HArr(arr, oa.shape);
      } else {
        Arr oa = (Arr) a;
        Arr ow = (Arr) w;
        if (oa.rank != ow.rank) throw new LengthError("ranks don't equal (shapes: "+ Main.formatAPL(oa.shape)+" vs "+ Main.formatAPL(ow.shape) +")", w);
        if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes don't match ("+ Main.formatAPL(oa.shape)+" vs "+ Main.formatAPL(ow.shape) +")", w);
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = scalar(f, oa.get(i), ow.get(i));
        }
        return new HArr(arr, oa.shape);
      }
    }
  }
  
  public interface DyNumVecFun {
    double call(double a, double w);
    default void call(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) {
        res[i] = call(a, w[i]);
      }
    }
    default void call(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) {
        res[i] = call(a[i], w);
      }
    }
    default void call(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) {
        res[i] = call(a[i], w[i]);
      }
    }
  }
  protected Value scalarNum(DyNumVecFun f, Value a, Value w) {
    if (a instanceof DoubleArr && w instanceof Num) {
      double[] res = new double[a.ia];
      f.call(res, a.asDoubleArr(), w.asDouble());
      return new DoubleArr(res, a.shape);
    }
    if (a instanceof Num && w instanceof DoubleArr) {
      double[] res = new double[w.ia];
      f.call(res, a.asDouble(), w.asDoubleArr());
      return new DoubleArr(res, w.shape);
    }
    if (a instanceof DoubleArr && w instanceof DoubleArr) {
      if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes of ⍺ & ⍵ don't match", w);
      double[] res = new double[w.ia];
      f.call(res, a.asDoubleArr(), w.asDoubleArr());
      return new DoubleArr(res, a.shape);
    }
    if (a instanceof Primitive) {
      if (w instanceof Primitive) {
        return new Num(f.call(a.asDouble(), w.asDouble()));
      } else {
        Arr ow = (Arr) w;
        Value[] arr = new Value[ow.ia];
        for (int i = 0; i < ow.ia; i++) {
          arr[i] = scalarNum(f, a, ow.get(i));
        }
        return new HArr(arr, ow.shape);
      }
    } else {
      if (w instanceof Primitive) {
        Arr oa = (Arr) a;
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = scalarNum(f, oa.get(i), w);
        }
        return new HArr(arr, oa.shape);
      } else {
        Arr oa = (Arr) a;
        Arr ow = (Arr) w;
        if (oa.rank != ow.rank) throw new LengthError("ranks don't equal (shapes: " + Main.formatAPL(oa.shape) + " vs " + Main.formatAPL(ow.shape) + ")", w);
        if (!Arrays.equals(oa.shape, ow.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(oa.shape) + " vs " + Main.formatAPL(ow.shape) + ")", w);
        Value[] arr = new Value[oa.ia];
        for (int i = 0; i < oa.ia; i++) {
          arr[i] = scalarNum(f, oa.get(i), ow.get(i));
        }
        return new HArr(arr, oa.shape);
      }
    }
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
}
