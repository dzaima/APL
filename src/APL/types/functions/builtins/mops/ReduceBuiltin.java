package APL.types.functions.builtins.mops;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.DimMMop;
import APL.types.functions.*;
import APL.types.functions.builtins.fns.*;

public class ReduceBuiltin extends Mop implements DimMMop {
  @Override public String repr() {
    return "/";
  }
  
  
  
  @Override
  public Value call(Obj f, Value w, int dim) {
    if (!(f instanceof Fun)) throw new SyntaxError("/ is only reduce. To use as replicate, use ⌿", f);
    return ngnReduce(w, dim, (Fun)f);
  }
  
  public Value call(Obj f, Value w, DerivedMop derv) {
    if (!(f instanceof Fun)) throw new SyntaxError("/ is only reduce. To use as replicate, use ⌿", f);
    Fun ff = (Fun) f;
    if (w.rank >= 2) {
      return ngnReduce(w, -1, ff);
    }
    if (w.quickDoubleArr()) {
      if (f instanceof PlusBuiltin) return new Num(w.sum());
      if (f instanceof MulBuiltin) {
        double p = 1;
        for (double d : w.asDoubleArr()) p*= d;
        return new Num(p);
      }
      if (f instanceof FloorBuiltin) {
        double p = Double.POSITIVE_INFINITY;
        for (double d : w.asDoubleArr()) p = Math.min(p, d);
        return new Num(p);
      }
      if (f instanceof CeilingBuiltin) {
        double p = Double.NEGATIVE_INFINITY;
        for (double d : w.asDoubleArr()) p = Math.max(p, d);
        return new Num(p);
      }
    }
    if (f instanceof CatBuiltin) {
      if (w.ia > 0) {
        special: {
          Value first = w.first();
          int am = 0;
          int chki = 0;
          typed: {
            if (first instanceof ChrArr || first instanceof Char) {
              for (Value v : w) {
                if (v.rank > 1) break special;
                if (!(v instanceof ChrArr || v instanceof Char)) break typed;
                am += v.ia;
                chki++;
              }
              char[] cs = new char[am];
              int ri = 0;
              for (int i = 0; i < w.ia; i++) {
                Value v = w.get(i);
                if (v instanceof Char) cs[ri++] = ((Char) v).chr;
                else {
                  String s = ((ChrArr) v).s;
                  s.getChars(0, s.length(), cs, ri);
                  ri+= s.length();
                }
              }
              return Main.toAPL(new String(cs));
              
              
            } else if (first.quickDoubleArr()) {
              for (Value v : w) {
                if (v.rank > 1) break special;
                if (!v.quickDoubleArr()) break typed;
                am+= v.ia;
                chki++;
              }
              double[] ds = new double[am];
              
              int ri = 0;
              for (int i = 0; i < w.ia; i++) {
                Value v = w.get(i);
                if (v instanceof Num) ds[ri++] = ((Num) v).num;
                else if (v.quickDoubleArr()) {
                  System.arraycopy(v.asDoubleArr(), 0, ds, ri, v.ia);
                  ri+= v.ia;
                }
              }
              return new DoubleArr(ds);
            }
          }
          
          for (; chki < w.ia; chki++) {
            Value v = w.get(chki);
            if (v.rank > 1) break special;
            am+= v.ia;
          }
          
          Value[] vs = new Value[am];
          int ri = 0;
          for (Value v : w) {
            System.arraycopy(v.values(), 0, vs, ri, v.ia);
            ri+= v.ia;
          }
          return HArr.create(vs);
        }
      }
    }
    Value[] a = w.values();
    if (a.length == 0) {
      Value id = ff.identity();
      if (id == null) throw new DomainError("No identity defined for "+f.name(), this, f);
      return id;
    }
    Value last = a[a.length-1];
    for (int i = a.length-2; i >= 0; i--) {
      last = ff.call(a[i], last);
    }
    return last.squeeze();
  }
  
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    Fun ff = isFn(f);
    if (w.rank != 1) throw new NYIError("A f/ B with 2≤⍴⍴B hasn't been implemented", this, w);
    int n = a.asInt();
    Value[] ra = new Value[w.ia - Math.abs(n) + 1];
    Value[] wa = w.values();
    if (n > 0) {
      for (int i = 0; i < ra.length; i++) {
        Value r = wa[i+n-1];
        for (int j = n-2; j >= 0; j--) {
          r = ff.call(wa[i + j], r);
        }
        ra[i] = r;
      }
    } else {
      n = -n;
      for (int i = 0; i < ra.length; i++) {
        Value r = wa[i];
        for (int j = 1; j < n; j++) {
          r = ff.call(wa[i + j], r);
        }
        ra[i] = r;
      }
    }
    return Arr.create(ra);
  }
  
  private Value ngnReduce(Value x, int axis, Fun f) { // https://chat.stackexchange.com/transcript/message/47158587#47158587
    if (x.rank == 0) return x;
    if (axis < 0) axis+= x.rank;
    int n0 = 1; // product of all dimensions before "axis"
    for (int i = 0; i < axis; i++) {
      n0*= x.shape[i];
    }
    int n1 = x.shape[axis]; // the dimension at "axis" - what's getting removed/reduced
    int n2 = x.ia / (n1*n0); // product of the rest of the shape
    int[] ns = x.eraseDim(axis);
    
    Value[] r = new Value[n0 * n2];
    for (int i = 0; i < n0; i++) {
      for (int k = 0; k < n2; k++) {
        Value c = x.get(i*n1*n2 + (n1-1)*n2 + k);
        for (int j = n1 - 2; j >= 0; j--) {
          c = f.call(x.get(i*n1*n2 + j*n2 + k), c);
        }
        r[i*n2 + k] = c.squeeze();
      }
    }
    return Arr.create(r, ns);
  }
}