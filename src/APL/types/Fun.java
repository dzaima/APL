package APL.types;

import APL.*;
import APL.errors.*;
import APL.types.arrs.*;

import java.util.*;

@SuppressWarnings({"unused", "Convert2streamapi"}) // class for getting overridden & being fast so no streams
public abstract class Fun extends Scopeable {
  public final int valid; // 0x niladic dyadic monadic
  
  public Value identity() {
    return null;
  }
  
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
  
  
  public interface AllMV {
    Value call(Value w);
  }
  public interface NumMV {
    Value call(Num w);
    default boolean retNum() {
      return true;
    }
    default double call(double w) {
      return call(new Num(w)).asDouble();
    }
    @SuppressWarnings("Java8ArraySetAll")
    default void call(double[] res, double[] a) {
      for (int i = 0; i < res.length; i++) res[i] = call(a[i]);
    }
  }
  public interface ChrMV {
    Value call(Char w);
    default Arr call(ChrArr a) {
      Value[] res = new Value[a.ia];
      for (int i = 0; i < a.ia; i++) res[i] = call(new Char(a.s.charAt(i)));
      return new HArr(res, a.shape);
    }
  }
  public interface MapMV {
    Value call(APLMap w);
  }
  
  
  protected Value allM(AllMV f, Value w) {
    if (w instanceof Primitive) {
      return f.call(w);
    } else {
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = allM(f, o.get(i));
      }
      return new HArr(arr, o.shape);
    }
  }
  protected Value numM(NumMV nf, Value w) {
    if (w instanceof Arr) {
      if (w.quickDoubleArr()) {
        double[] res = new double[w.ia];
        nf.call(res, w.asDoubleArr());
        return new DoubleArr(res, w.shape);
      }
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numM(nf, o.get(i));
      }
      return new HArr(arr, o.shape);
    } else if (w instanceof Num) return nf.call((Num) w);
    else throw new DomainError("Expected number, got "+w.humanType(false), w);
  }
  
  protected Value numChrM(NumMV nf, ChrMV cf, Value w) {
    if (w instanceof Arr) {
      if (w instanceof DoubleArr && nf.retNum()) {
        double[] res = new double[w.ia];
        nf.call(res, w.asDoubleArr());
        return new DoubleArr(res, w.shape);
      }
      if (w instanceof ChrArr) return cf.call((ChrArr) w);
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numChrM(nf, cf, o.get(i));
      }
      return new HArr(arr, o.shape);
    } else if (w instanceof Char) return cf.call((Char)w);
    else if (w instanceof Num) return nf.call((Num) w);
    else throw new DomainError("Expected either number or character argument, got "+w.humanType(false), w);
  }
  
  protected Value numChrMapM(NumMV nf, ChrMV cf, MapMV mf, Value w) {
    if (w instanceof Arr) {
      if (w.quickDoubleArr()) {
        double[] res = new double[w.ia];
        nf.call(res, w.asDoubleArr());
        return new DoubleArr(res, w.shape);
      }
      Arr o = (Arr) w;
      Value[] arr = new Value[o.ia];
      for (int i = 0; i < o.ia; i++) {
        arr[i] = numChrM(nf, cf, o.get(i));
      }
      return new HArr(arr, o.shape);
    } else if (w instanceof Char  ) return cf.call((Char  ) w);
      else if (w instanceof Num   ) return nf.call((Num   ) w);
      else if (w instanceof APLMap) return mf.call((APLMap) w);
    else throw new DomainError("Expected either number/char/map, got "+w.humanType(false), w);
  }
  
  
  
  
  
  
  
  public interface AllDV {
    Value call(Value a, Value w);
  }
  public interface NumDV {
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
  public interface ChrDV {
    Value call(char a, char w);
  }
  
  
  protected Value allM(AllDV f, Value a, Value w) {
    if (a instanceof Primitive && w instanceof Primitive) return f.call(a, w);
  
    if (a.scalar()) {
      Value fst_a = a.first();
      
      if (w.scalar()) {
        return new Rank0Arr(allM(f, fst_a, w.first()));
    
      } else {
        Value[] arr = new Value[w.ia];
        Iterator<Value> iterator = w.iterator();
        for (int i = 0; i < w.ia; i++) {
          arr[i] = allM(f, fst_a, iterator.next());
        }
        return new HArr(arr, w.shape);
        
      }
    } else {
      if (w.scalar()) {
        Value fst_w = w.first();
  
        Value[] arr = new Value[a.ia];
        Iterator<Value> iterator = a.iterator();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = allM(f, iterator.next(), fst_w);
        }
        return new HArr(arr, a.shape);
        
      } else {
        if (a.rank != w.rank) throw new LengthError("ranks don't equal (shapes: " + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        assert a.ia == w.ia;
        Value[] arr = new Value[a.ia];
        Iterator<Value> ai = a.iterator();
        Iterator<Value> wi = w.iterator();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = allM(f, ai.next(), wi.next());
        }
        return new HArr(arr, a.shape);
        
      }
    }
  }
  protected Value numD(NumDV f, Value a, Value w) {
    if (a.quickDoubleArr() && !a.scalar() && w instanceof Num) {
      double[] res = new double[a.ia];
      f.call(res, a.asDoubleArr(), w.asDouble());
      return new DoubleArr(res, a.shape);
    }
    if (a instanceof Num && w.quickDoubleArr() && !w.scalar()) {
      double[] res = new double[w.ia];
      f.call(res, a.asDouble(), w.asDoubleArr());
      return new DoubleArr(res, w.shape);
    }
    if (a.quickDoubleArr() && w.quickDoubleArr() && !a.scalar() && !w.scalar()) {
      if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
      double[] res = new double[w.ia];
      f.call(res, a.asDoubleArr(), w.asDoubleArr());
      return new DoubleArr(res, a.shape);
    }
    
    if (a instanceof Num && w instanceof Num) return new Num(f.call(a.asDouble(), w.asDouble()));
    
    
    if (a.scalar()) {
      Value fst_a = a.first();
  
      if (w.scalar()) {
        if (w instanceof Primitive) throw new DomainError("calling a number-only function with "+w.humanType(true));
        return new Rank0Arr(numD(f, fst_a, w.first()));
    
      } else {
        Value[] arr = new Value[w.ia];
        Iterator<Value> iterator = w.iterator();
        for (int i = 0; i < w.ia; i++) {
          arr[i] = numD(f, fst_a, iterator.next());
        }
        return new HArr(arr, w.shape);
        
      }
    } else {
      if (w.scalar()) {
        Value[] arr = new Value[a.ia];
        Iterator<Value> iterator = a.iterator();
        Value fst_w = w.first();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = numD(f, iterator.next(), fst_w);
        }
        return new HArr(arr, a.shape);
        
      } else {
        if (a.rank != w.rank) throw new LengthError("ranks don't equal (shapes: " + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        assert a.ia == w.ia;
        Value[] arr = new Value[a.ia];
        Iterator<Value> ai = a.iterator();
        Iterator<Value> wi = w.iterator();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = numD(f, ai.next(), wi.next());
        }
        return new HArr(arr, a.shape);
        
      }
    }
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
}
