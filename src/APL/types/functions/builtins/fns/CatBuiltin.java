package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.DimDFn;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class CatBuiltin extends Builtin implements DimDFn {
  @Override public String repr() {
    return ",";
  }
  
  
  public Value call(Value w) {
    if (w instanceof Primitive) {
      if (w instanceof Num) return new DoubleArr(new double[]{((Num) w).num});
      if (w instanceof Char) return new ChrArr(String.valueOf(((Char) w).chr));
      return new Shape1Arr(w);
    }
    return w.ofShape(new int[]{w.ia});
  }
  public Value call(Value a, Value w) {
    int dim = Math.max(a.rank, w.rank) - 1;
    if (a.rank <= 1 && w.rank <= 1) {
      if ((a instanceof BitArr || Main.isBool(a))
       && (w instanceof BitArr || Main.isBool(w))) {
        return catBit(a, w);
      }
      if (a instanceof DoubleArr && w instanceof DoubleArr) {
        double[] r = new double[a.ia + w.ia];
        System.arraycopy(a.asDoubleArr(), 0, r, 0, a.ia);
        System.arraycopy(w.asDoubleArr(), 0, r, a.ia, w.ia);
        return new DoubleArr(r);
      }
      Value[] r = new Value[a.ia + w.ia];
      System.arraycopy(a.values(), 0, r, 0, a.ia);
      System.arraycopy(w.values(), 0, r, a.ia, w.ia);
      return Arr.create(r);
    }
    return cat(a, w, dim);
  }
  public Value call(Value a, Value w, int dim) {
    if (dim < 0 || dim >= Math.max(a.rank, w.rank)) throw new DomainError("dimension "+dim+" is out of range");
    return cat(a, w, dim);
  }
  private static BitArr catBit(Value a, Value w) { // for ranks <= 1
    boolean ab = a instanceof BitArr;
    boolean wb = w instanceof BitArr;
    int sz = a.ia + w.ia;
  
    BitArr.BA res = new BitArr.BA(sz);
    if (ab) res.add((BitArr) a);
    else    res.add(Main.bool(a));
    if (wb) res.add((BitArr) w);
    else    res.add(Main.bool(w));
  
    return res.finish();
  }
  static Value cat(Value a, Value w, int k) {
    if (a.rank<=1 && w.rank<=1
      && (a instanceof BitArr || Main.isBool(a))
      && (w instanceof BitArr || Main.isBool(w))) {
      return catBit(a, w);
    }
    boolean aScalar = a.scalar(), wScalar = w.scalar();
    if (aScalar && wScalar) return cat(new Shape1Arr(a.first()  ), w, 0);
    if (!aScalar && !wScalar) {
      if (a.rank != w.rank) throw new RankError("ranks not matchable", w);
      for (int i = 0; i < a.rank; i++) {
        if (i != k && a.shape[i] != w.shape[i]) throw new LengthError("lengths not matchable ("+new DoubleArr(a.shape)+" vs "+new DoubleArr(w.shape)+")", w);
      }
    }
    int[] rs = !aScalar ? a.shape.clone() : w.shape.clone(); // shape of the result
    rs[k] += aScalar || wScalar ? 1 : w.shape[k];
    int n0 = 1; for (int i = 0; i < k; i++) n0 *= rs[i];             // product of major dimensions
    int n1 = rs[k];                                                  // dimension to catenate on
    int n2 = 1; for (int i = k + 1; i < rs.length; i++) n2 *= rs[i]; // product of minor dimensions
    int ad = aScalar ? n2 : a.shape[k] * n2;                         // chunk size for ⍺
    int wd = wScalar ? n2 : w.shape[k] * n2;                         // chunk size for ⍵
  
    if (a.quickDoubleArr() && w.quickDoubleArr()) {
      double[] rv = new double[n0 * n1 * n2];                            // result values
      copyChunksD(aScalar, a.asDoubleArr(), rv,  0, ad, ad + wd);
      copyChunksD(wScalar, w.asDoubleArr(), rv, ad, wd, ad + wd);
      return new DoubleArr(rv, rs);
    } else {
      Value[] rv = new Value[n0 * n1 * n2];                            // result values
      copyChunks(aScalar, a.values(), rv, 0, ad, ad + wd);
      copyChunks(wScalar, w.values(), rv, ad, wd, ad + wd);
      return Arr.create(rv, rs);
    }
  }
  private static void copyChunks(boolean scalar, Value[] av, Value[] rv, int offset, int ad, int rd) {
    if (scalar) {
      for (int i = offset; i < rv.length; i += rd) {
        Arrays.fill(rv, i, i + ad, av[0]);
      }
    } else {
      for (int i = offset, j = 0; i < rv.length; i += rd, j += ad) { // i:position in rv, j:position in av
        System.arraycopy(av, j, rv, i, ad);
      }
    }
  }
  
  private static void copyChunksD(boolean scalar, double[] av, double[] rv, int offset, int ad, int rd) {
    if (scalar) {
      for (int i = offset; i < rv.length; i += rd) {
        Arrays.fill(rv, i, i + ad, av[0]);
      }
    } else {
      for (int i = offset, j = 0; i < rv.length; i += rd, j += ad) { // i:position in rv, j:position in av
        System.arraycopy(av, j, rv, i, ad);
      }
    }
  }
  
  
  
  public Value under(Obj o, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(w)) : (Value) o;
    if (v.ia != w.ia) throw new DomainError("⍢, expected equal amount of output & output items", this);
    return v.ofShape(w.shape);
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(a, w)) : (Value) o;
    if (a.rank>1) throw new NYIError(", inverted on rank "+a.rank+" ⍺", this);
    if (v.rank>1) throw new NYIError(", inverted on rank "+v.rank+" ⍵", this);
    for (int i = 0; i < a.ia; i++) {
      if (a.get(i) != v.get(i)) throw new DomainError("inverting , received non-equal prefixes");
    }
    if (w.rank==0) {
      if (a.ia+1 != v.ia) throw new DomainError("original ⍵ was of rank ⍬, which is not satisfiable", this);
      return v.get(v.ia-1);
    }
    return DownArrowBuiltin.on(Num.of(a.ia), v, 0);
  }
}
