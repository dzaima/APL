package APL.types;

import APL.*;
import APL.errors.*;
import APL.types.arrs.*;

import java.util.*;

@SuppressWarnings({"Convert2streamapi", "Java8ArraySetAll"}) // class for getting overridden & being fast so no streams
public abstract class Fun extends Scopeable {
  
  public Value identity() {
    return null;
  }
  
  protected Fun(Scope sc) {
    super(sc);
  }
  protected Fun() {
    super(null);
  }
  public Obj call(Value w) {
    throw new IncorrectArgsError("function "+toString()+" called monadically", this, w);
  }
  public Obj call(Value a, Value w) {
    throw new IncorrectArgsError("function "+toString()+" called dyadically", this, a);
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
  
  public interface NumMV {
    Value call(Num w);
    default boolean retNum() {
      return true;
    }
    default double call(double w) {
      return call(new Num(w)).asDouble();
    }
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
  
  public interface AllMV {
    Value call(Value w);
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
    else throw new DomainError("Expected number, got "+w.humanType(false), this, w);
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
    else throw new DomainError("Expected either number or character argument, got "+w.humanType(false), this, w);
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
    else throw new DomainError("Expected either number/char/map, got "+w.humanType(false), this, w);
  }
  
  
  protected Value allD(D_AA f, Value a, Value w) {
    if (a instanceof Primitive && w instanceof Primitive) return f.call(a, w);
    
    if (a.scalar()) {
      Value af = a.first();
      
      if (w.scalar()) {
        return new Rank0Arr(allD(f, af, w.first()));
        
      } else {
        Value[] arr = new Value[w.ia];
        Iterator<Value> wi = w.iterator();
        for (int i = 0; i < w.ia; i++) {
          arr[i] = allD(f, af, wi.next());
        }
        return new HArr(arr, w.shape);
        
      }
    } else {
      if (w.scalar()) {
        Value wf = w.first();
        
        Value[] arr = new Value[a.ia];
        Iterator<Value> ai = a.iterator();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = allD(f, ai.next(), wf);
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
          arr[i] = allD(f, ai.next(), wi.next());
        }
        return new HArr(arr, a.shape);
        
      }
    }
  }
  
  
  
  
  
  
  
  public interface D_AA {
    Value call(Value a, Value w);
  }
  public abstract static class D_NNeN implements D_NN { // dyadic number-number equals number
    public abstract double on(double a, double w);
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) {
        res[i] = on(a, w[i]);
      }
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) {
        res[i] = on(a[i], w);
      }
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) {
        res[i] = on(a[i], w[i]);
      }
    }
    
    public Value call(double a, double w) {
      return new Num(on(a, w));
    }
    public Value call(double[] a, double[] w, int[] sh) {
      double[] res = new double[w.length];
      on(res, a, w);
      return new DoubleArr(res, sh);
    }
    public Value call(double a, double[] w, int[] sh) {
      double[] res = new double[w.length];
      on(res, a, w);
      return new DoubleArr(res, sh);
    }
    public Value call(double[] a, double w, int[] sh) {
      double[] res = new double[a.length];
      on(res, a, w);
      return new DoubleArr(res, sh);
    }
  }
  
  public abstract static class D_NNeB implements D_NN { // dyadic number-number equals boolean
    public abstract boolean on(double a, double w);
    public abstract void on(BitArr.BC res, double a, double[] w);
    public abstract void on(BitArr.BC res, double[] a, double w);
    public abstract void on(BitArr.BC res, double[] a, double[] w);
    
    public Value call(double a, double w) {
      return on(a, w)? Num.ONE : Num.ZERO;
    }
    public Value call(double[] a, double[] w, int[] sh) {
      BitArr.BC res = BitArr.create(sh);
      on(res, a, w);
      return res.finish();
    }
    public Value call(double a, double[] w, int[] sh) {
      BitArr.BC res = BitArr.create(sh);
      on(res, a, w);
      return res.finish();
    }
    public Value call(double[] a, double w, int[] sh) {
      BitArr.BC res = BitArr.create(sh);
      on(res, a, w);
      return res.finish();
    }
  }
  
  
  public interface D_NN {
    Value call(double   a, double   w);
    Value call(double[] a, double[] w, int[] sh);
    Value call(double   a, double[] w, int[] sh);
    Value call(double[] a, double   w, int[] sh);
  }
  public interface D_BB {
    Value call(BitArr  a, BitArr  w);
    Value call(boolean a, BitArr  w);
    Value call(BitArr  a, boolean w);
  }
  public interface D_CC {
    Value call(char a, char w);
  }
  
  
  protected Value numD(D_NN f, Value a, Value w) {
    if (a.scalar()) {
      if (w.scalar()) { // ⊃⍺ ⊃⍵
        if (a instanceof Primitive & w instanceof Primitive) {
          if (a instanceof Num & w instanceof Num) return f.call(((Num) a).num, ((Num) w).num);
          else throw new DomainError("calling a number-only function with "+w.humanType(true));
        } else return new Rank0Arr(numD(f, a.first(), w.first()));
        
      } else { // ⍺¨ ⍵
        if (w.quickDoubleArr() && a instanceof Primitive) {
          return f.call(a.asDouble(), w.asDoubleArr(), w.shape);
        }
        Value af = a.first();
        Iterator<Value> wi = w.iterator();
        Value[] vs = new Value[w.ia];
        for (int i = 0; i < w.ia; i++) {
          vs[i] = numD(f, af, wi.next());
        }
        return new HArr(vs, w.shape);
        
      }
    } else {
      if (w.scalar()) { // ⍺ ⍵¨
        if (a.quickDoubleArr() && w instanceof Primitive) {
          return f.call(a.asDoubleArr(), w.asDouble(), a.shape);
        }
        Value wf = w.first();
        Iterator<Value> ai = a.iterator();
        Value[] vs = new Value[a.ia];
        for (int i = 0; i < a.ia; i++) {
          vs[i] = numD(f, ai.next(), wf);
        }
        
        return new HArr(vs, a.shape);
        
      } else { // ⍺ ¨ ⍵
        if (a.rank != w.rank) throw new LengthError("ranks don't equal (shapes: " + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        
        if (a.quickDoubleArr() && w.quickDoubleArr()) {
          return f.call(a.asDoubleArr(), w.asDoubleArr(), a.shape);
        }
        
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
  protected Value bitD(D_NN n, D_BB b, Value a, Value w) {
    if (a.scalar()) {
      if (w.scalar()) { // ⊃⍺ ⊃⍵
        if (a instanceof Primitive & w instanceof Primitive) {
          if (a instanceof Num & w instanceof Num) return n.call(((Num) a).num, ((Num) w).num);
          else throw new DomainError("calling a number-only function with "+w.humanType(true));
        } else return new Rank0Arr(numD(n, a.first(), w.first()));
        
      } else { // ⍺¨ ⍵
        if (a instanceof Primitive) {
          if (w.quickDoubleArr()) {
            return n.call(a.asDouble(), w.asDoubleArr(), w.shape);
          }
          if (w instanceof BitArr) {
            return b.call(Main.bool(a), ((BitArr) w));
          }
        }
        Value af = a.first();
        Iterator<Value> wi = w.iterator();
        Value[] vs = new Value[w.ia];
        for (int i = 0; i < w.ia; i++) {
          vs[i] = numD(n, af, wi.next());
        }
        return new HArr(vs, w.shape);
        
      }
    } else {
      if (w.scalar()) { // ⍺ ⍵¨
        if (w instanceof Primitive) {
          if (a.quickDoubleArr()) {
            return n.call(a.asDoubleArr(), w.asDouble(), a.shape);
          }
          if (a instanceof BitArr) {
            return b.call((BitArr) a, Main.bool(w));
          }
        }
        Value wf = w.first();
        Iterator<Value> ai = a.iterator();
        Value[] vs = new Value[a.ia];
        for (int i = 0; i < a.ia; i++) {
          vs[i] = numD(n, ai.next(), wf);
        }
        
        return new HArr(vs, a.shape);
        
      } else { // ⍺ ¨ ⍵
        if (a.rank != w.rank) throw new LengthError("ranks don't equal (shapes: " + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        
        if (a.quickDoubleArr() && w.quickDoubleArr()) {
          return n.call(a.asDoubleArr(), w.asDoubleArr(), a.shape);
        }
        if (a instanceof BitArr && b instanceof BitArr) {
          return b.call((BitArr) a, (BitArr) w);
        }
        
        Value[] arr = new Value[a.ia];
        Iterator<Value> ai = a.iterator();
        Iterator<Value> wi = w.iterator();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = numD(n, ai.next(), wi.next());
        }
        return new HArr(arr, a.shape);
        
      }
    }
  }
  
  
  protected Value numChrD(D_NN n, D_CC c, D_AA def, Value a, Value w) {
    if (a.scalar()) {
      if (w.scalar()) { // ⊃⍺ ⊃⍵
        if (a instanceof Primitive & w instanceof Primitive) {
          if (a instanceof Num & w instanceof Num) return n.call(((Num) a).num, ((Num) w).num);
          else if (a instanceof Char & w instanceof Char) return c.call(((Char) a).chr, ((Char) w).chr);
          else return def.call(a, w);
        } else return new Rank0Arr(numChrD(n, c, def, a.first(), w.first()));
        
      } else { // ⍺¨ ⍵
        if (a instanceof Primitive && w.quickDoubleArr()) {
          return n.call(a.asDouble(), w.asDoubleArr(), w.shape);
        }
        
        Value af = a.first();
        Iterator<Value> wi = w.iterator();
        Value[] vs = new Value[w.ia];
        for (int i = 0; i < w.ia; i++) {
          vs[i] = numChrD(n, c, def, af, wi.next());
        }
        return new HArr(vs, w.shape);
        
      }
    } else {
      if (w.scalar()) { // ⍺ ⍵¨
        if (w instanceof Primitive && a.quickDoubleArr()) {
          return n.call(a.asDoubleArr(), w.asDouble(), a.shape);
        }
        Value wf = w.first();
        Iterator<Value> ai = a.iterator();
        Value[] vs = new Value[a.ia];
        for (int i = 0; i < a.ia; i++) {
          vs[i] = numChrD(n, c, def, ai.next(), wf);
        }
        
        return new HArr(vs, a.shape);
      } else { // ⍺ ¨ ⍵
        if (a.rank != w.rank) throw new LengthError("ranks don't equal (shapes: " + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        
        if (a.quickDoubleArr() && w.quickDoubleArr()) {
          return n.call(a.asDoubleArr(), w.asDoubleArr(), a.shape);
        }
        
        Value[] arr = new Value[a.ia];
        Iterator<Value> ai = a.iterator();
        Iterator<Value> wi = w.iterator();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = numChrD(n, c, def, ai.next(), wi.next());
        }
        return new HArr(arr, a.shape);
        
      }
    }
  }
  protected Value ncbaD(D_NN n, D_BB b, D_CC c, D_AA def, Value a, Value w) {
    if (a.scalar()) {
      if (w.scalar()) { // ⊃⍺ ⊃⍵
        if (a instanceof Primitive & w instanceof Primitive) {
          if (a instanceof Num & w instanceof Num) return n.call(((Num) a).num, ((Num) w).num);
          else if (a instanceof Char & w instanceof Char) return c.call(((Char) a).chr, ((Char) w).chr);
          else return def.call(a, w);
        } else return new Rank0Arr(ncbaD(n, b, c, def, a.first(), w.first()));
        
      } else { // ⍺¨ ⍵
        if (a instanceof Primitive) {
          if (w.quickDoubleArr()) {
            return n.call(a.asDouble(), w.asDoubleArr(), w.shape);
          }
          if (w instanceof BitArr) {
            return b.call(Main.bool(a), ((BitArr) w));
          }
        }
        
        Value af = a.first();
        Iterator<Value> wi = w.iterator();
        Value[] vs = new Value[w.ia];
        for (int i = 0; i < w.ia; i++) {
          vs[i] = ncbaD(n, b, c, def, af, wi.next());
        }
        return new HArr(vs, w.shape);
      }
    } else {
      if (w.scalar()) { // ⍺ ⍵¨
        if (w instanceof Primitive) {
          if (a.quickDoubleArr()) {
            return n.call(a.asDoubleArr(), w.asDouble(), a.shape);
          }
          if (a instanceof BitArr) {
            return b.call((BitArr) a, Main.bool(w));
          }
        }
        Value wf = w.first();
        Iterator<Value> ai = a.iterator();
        Value[] vs = new Value[a.ia];
        for (int i = 0; i < a.ia; i++) {
          vs[i] = ncbaD(n, b, c, def, ai.next(), wf);
        }
        
        return new HArr(vs, a.shape);
        
      } else { // ⍺ ¨ ⍵
        if (a.rank != w.rank) throw new LengthError("ranks don't equal (shapes: " + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes don't match (" + Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")", w);
        
        if (a.quickDoubleArr() && w.quickDoubleArr()) {
          return n.call(a.asDoubleArr(), w.asDoubleArr(), a.shape);
        }
        if (a instanceof BitArr && b instanceof BitArr) {
          return b.call((BitArr) a, (BitArr) w);
        }
        
        Value[] arr = new Value[a.ia];
        Iterator<Value> ai = a.iterator();
        Iterator<Value> wi = w.iterator();
        for (int i = 0; i < a.ia; i++) {
          arr[i] = ncbaD(n, b, c, def, ai.next(), wi.next());
        }
        return new HArr(arr, a.shape);
        
      }
    }
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
  
  
  public abstract String repr();
  
  @Override public String toString() {
    return repr();
  }
}
