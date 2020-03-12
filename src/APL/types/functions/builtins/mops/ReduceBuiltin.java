package APL.types.functions.builtins.mops;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.DimMMop;
import APL.types.functions.*;
import APL.types.functions.builtins.fns.*;

import java.util.ArrayList;

public class ReduceBuiltin extends Mop implements DimMMop {
  @Override public String repr() {
    return "/";
  }
  
  
  
  @Override
  public Obj call(Obj f, Value w, int dim) {
    if (!(f instanceof Fun)) throw new SyntaxError("/ is only reduce. To use as replicate, use ⌿", f);
    return ngnReduce(w, dim, (Fun)f);
  }
  
  public Obj call(Obj f, Value w, DerivedMop derv) {
    if (!(f instanceof Fun)) throw new SyntaxError("/ is only reduce. To use as replicate, use ⌿", f);
    Fun ff = (Fun) f;
    if (w.rank >= 2) {
      return ngnReduce(w, -1, ff);
    }
    if (w.quickDoubleArr()) {
      if (f instanceof PlusBuiltin) return new Num(w.sum());
      if (f instanceof MulBuiltin) {
        double p = 1;
        for (double d : w.asDoubleArr()) p *= d;
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
          ArrayList<Value> pre = null;
          int si = 0;
          typed: {
            if (first instanceof ChrArr || first instanceof Char) {
              StringBuilder res = new StringBuilder();
              for (int i = 0; i < w.ia; i++) {
                Value v = w.get(i);
                if (v.rank > 1) break special; // oh nooo
                if (v instanceof Char) res.append(((Char) v).chr);
                else if (v instanceof ChrArr) res.append(((ChrArr) v).s);
                else {
                  si = i;
                  pre = new ArrayList<>();
                  for (int j = 0; j < res.length(); j++) {
                    pre.add(Char.of(res.charAt(j)));
                  }
                  break typed;
                }
              }
              return Main.toAPL(res.toString());
            }
            if (first.quickDoubleArr()) {
              ArrayList<Double> ds = new ArrayList<>();
              for (int i = 0; i < w.ia; i++) {
                Value v = w.get(i);
                if (v.rank > 1) break special; // :/
                if (v instanceof Num) ds.add(((Num) v).num);
                else if (v instanceof DoubleArr) for (double d : ((DoubleArr) v).arr) ds.add(d);
                else {
                  si = i;
                  pre = new ArrayList<>();
                  for (Double d : ds) {
                    pre.add(new Num(d));
                  }
                  break typed;
                }
              }
              return new DoubleArr(ds);
            }
          }
          if (pre == null) pre = new ArrayList<>();
          
          for (int i = si; i < w.ia; i++) {
            Value v = w.get(i);
            if (v.rank > 1) break special; // :|
            for (Value c : v) pre.add(c);
          }
          return HArr.create(pre.toArray(new Value[0]));
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
      last = (Value) ff.call(a[i], last);
    }
    return last.squeeze();
  }
  
  public Obj call(Obj f, Value a, Value w, DerivedMop derv) {
    isFn(f);
    if (w.rank != 1) throw new NYIError("A f/ B with 2≤⍴⍴B hasn't been implemented", this, w);
    int n = a.asInt();
    Value[] ra = new Value[w.ia - Math.abs(n) + 1];
    Value[] wa = w.values();
    if (n > 0) {
      for (int i = 0; i < ra.length; i++) {
        Value r = wa[i+n-1];
        for (int j = n-2; j >= 0; j--) {
          r = (Value) ((Fun) f).call(wa[i + j], r);
        }
        ra[i] = r;
      }
    } else {
      n = -n;
      for (int i = 0; i < ra.length; i++) {
        Value r = wa[i];
        for (int j = 1; j < n; j++) {
          r = (Value) ((Fun) f).call(wa[i + j], r);
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
          c = (Value) f.call(x.get(i*n1*n2 + j*n2 + k), c);
        }
        r[i*n2 + k] = c.squeeze();
      }
    }
    return Arr.create(r, ns);
  }
}