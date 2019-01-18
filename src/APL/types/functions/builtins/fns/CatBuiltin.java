package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.DimDFn;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class CatBuiltin extends Builtin implements DimDFn {
  public CatBuiltin() {
    super(",");
  }
  public Obj call(Value w) {
    if (w instanceof Primitive) return new Shape1Arr(w);
    return w.ofShape(new int[]{w.ia});
  }
  public Obj call(Value a, Value w) {
    int dim = Math.max(a.rank, w.rank) - 1;
    return cat(a, w, dim);
  }
  public Obj call(Value a, Value w, int dim) {
    if (dim < 0 || dim >= Math.max(a.rank, w.rank)) throw new DomainError("dimension "+dim+" is out of range");
    return cat(a, w, dim);
  }
  static Obj cat(Value a, Value w, int k) {
    boolean aScalar = a.scalar(), wScalar = w.scalar();
    if (aScalar && wScalar) return cat(new Shape1Arr(a.first()  ), w, 0);
    if (!aScalar && !wScalar) {
      if (a.rank != w.rank) throw new RankError("ranks not matchable", w);
      for (int i = 0; i < a.rank; i++) {
        if (i != k && a.shape[i] != w.shape[i]) throw new LengthError("lengths not matchable", w);
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
  
}
