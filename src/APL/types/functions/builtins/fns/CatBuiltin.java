package APL.types.functions.builtins.fns;

import java.util.Arrays;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.DimDFn;
import APL.types.functions.Builtin;

public class CatBuiltin extends Builtin implements DimDFn {
  public CatBuiltin() {
    super(",", 0x011);
  }
  public Obj call(Value w) {
    if (w instanceof Primitive) return new Shape1Arr(w);
    return w.ofShape(new int[]{w.ia});
  }
  public Obj call(Value a, Value w) {
    return cat(a, w, Math.max(a.shape.length, w.shape.length) - 1);
  }
  public Obj call(Value a, Value w, int dim) {
    if (dim < 0 || dim >= Math.max(a.shape.length, w.shape.length)) throw new DomainError("dimension given is out of range");
    return cat(a, w, dim);
  }
  static Obj cat(Value a, Value w, int k) {
    boolean aScalar = a.scalar(), wScalar = w.scalar();
    if (aScalar && wScalar) return cat(new Shape1Arr(a.get(0)), w, 0);
    if (!aScalar && !wScalar) {
      if (a.shape.length != w.shape.length) throw new RankError("ranks not matchable", w);
      for (int i = 0; i < a.shape.length; i++) {
        if (i != k && a.shape[i] != w.shape[i]) throw new LengthError("lengths not matchable", w);
      }
    }
    int[] rs = !aScalar ? a.shape.clone() : !wScalar ? w.shape.clone() : new int[] {2}; // shape of the result
    rs[k] += aScalar || wScalar ? 1 : w.shape[k];
    int n0 = 1; for (int i = 0; i < k; i++) n0 *= rs[i];             // product of major dimensions
    int n1 = rs[k];                                                  // dimension to catenate on
    int n2 = 1; for (int i = k + 1; i < rs.length; i++) n2 *= rs[i]; // product of minor dimensions
    int ad = aScalar ? n2 : a.shape[k] * n2;                         // chunk size for ⍺
    int wd = wScalar ? n2 : w.shape[k] * n2;                         // chunk size for ⍵
    Value[] rv = new Value[n0 * n1 * n2];                            // result values
    copyChunks(aScalar, a.values(), rv,  0, ad, ad + wd);
    copyChunks(wScalar, w.values(), rv, ad, wd, ad + wd);
    return Arr.create(rv, rs); // TODO specialize for DoubleArr so this doesn't need squeezing
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
  
}
