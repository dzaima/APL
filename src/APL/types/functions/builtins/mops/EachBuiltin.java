package APL.types.functions.builtins.mops;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.*;
import APL.types.functions.builtins.fns.*;

import java.util.Arrays;

public class EachBuiltin extends Mop {
  @Override public String repr() {
    return "¨";
  }
  
  
  
  public Value call(Obj f, Value w, DerivedMop derv) {
    if (w.scalar()) return LShoeBuiltin.on(f instanceof Fun? ((Fun)f).call(w.first()) : (Value) f);
    if (f instanceof Fun) {
      Value[] n = new Value[w.ia];
      for (int i = 0; i < n.length; i++) {
        n[i] = ((Fun) f).call(w.get(i));
      }
      return Arr.create(n, w.shape);
    } else {
      return new SingleItemArr((Value) f, w.shape);
    }
  }
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    Fun ff = (Fun) f;
    if (w.scalar()) {
      Value w0 = w.first();
      if (a.scalar()) return LShoeBuiltin.on(ff.call(a.first(), w0));
      Value[] n = new Value[a.ia];
      for (int i = 0; i < n.length; i++) n[i] = ff.call(a.get(i), w0);
      return Arr.create(n, a.shape);
    }
    if (a.scalar()) {
      Value[] n = new Value[w.ia];
      Value a0 = a.first();
      for (int i = 0; i < n.length; i++) n[i] = ff.call(a0, w.get(i));
      return Arr.create(n, w.shape);
    }
    if (!Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes not equal ("+ Main.formatAPL(a.shape)+" vs "+Main.formatAPL(w.shape)+")", derv, w);
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ff.call(a.get(i), w.get(i));
    }
    return Arr.create(n, w.shape);
  }
  
  public Value callInv(Obj f, Value w) {
    if (!(f instanceof Fun)) throw new DomainError("can't invert A¨", this);
    Value[] n = new Value[w.ia];
    for (int i = 0; i < n.length; i++) {
      n[i] = ((Fun) f).callInv(w.get(i));
    }
    if (w.rank == 0 && n[0] instanceof Primitive) return n[0];
    return Arr.create(n, w.shape);
  }
  
  public Value under(Obj aa, Obj o, Value w, DerivedMop derv) {
    Fun aaf = isFn(aa);
    Value[] res2 = new Value[w.ia];
    rec(aaf, o, w, 0, new Value[w.ia], new Value[1], res2);
    return Arr.create(res2, w.shape);
  }
  
  private static void rec(Fun aa, Obj o, Value w, int i, Value[] args, Value[] resPre, Value[] res) {
    if (i == args.length) {
      Value v = o instanceof Fun? ((Fun) o).call(Arr.create(args, w.shape)) : (Value) o;
      resPre[0] = v;
    } else {
      res[i] = aa.under(new Fun() { public String repr() { return aa.repr()+"¨"; }
        public Value call(Value w1) {
          args[i] = w1;
          rec(aa, o, w, i+1, args, resPre, res);
          return resPre[0].get(i);
        }
      }, w.get(i));
    }
  }
  
  
  public Value underW(Obj aa, Obj o, Value a, Value w, DerivedMop derv) {
    return underW(isFn(aa), o, a, w, this);
  }
  
  public static Value underW(Fun aa, Obj o, Value a, Value w, Callable blame) {
    if (a.rank!=0 && w.rank!=0 && !Arrays.equals(a.shape, w.shape)) throw new LengthError("shapes not equal ("+ Main.formatAPL(a.shape)+" vs "+Main.formatAPL(w.shape)+")", blame, w);
    int ia = Math.max(a.ia, w.ia);
    Value[] res2 = new Value[ia];
    if (a.rank==0 && !(a instanceof Primitive)) a = new Rank0Arr(a.first()); // abuse that get doesn't check indexes for simple scalar extension
    if (w.rank==0 && !(w instanceof Primitive)) w = new Rank0Arr(a.first());
    rec(aa, o, a, w, 0, new Value[ia], new Value[1], res2);
    return Arr.create(res2, w.shape);
  }
  
  private static void rec(Fun aa, Obj o, Value a, Value w, int i, Value[] args, Value[] resPre, Value[] res) {
    if (i == args.length) {
      Value v = o instanceof Fun? ((Fun) o).call(Arr.create(args, w.shape)) : (Value) o;
      resPre[0] = v;
    } else {
      res[i] = aa.underW(new Fun() { public String repr() { return aa.repr()+"¨"; }
        public Value call(Value w1) {
          args[i] = w1;
          rec(aa, o, a, w, i+1, args, resPre, res);
          return resPre[0].get(i);
        }
      }, a.get(i), w.get(i));
    }
  }
}