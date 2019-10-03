package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.dimensions.*;
import APL.types.functions.Builtin;

public class ReverseBuiltin extends Builtin implements DimMFn, DimDFn {
  @Override public String repr() {
    return "⌽";
  }
  
  
  @Override public Obj call(Value w, int dim) {
    return ((Arr) w).reverseOn(-dim-1);
  }
  @Override public Obj call(Value w) {
    if (w instanceof Primitive) return w;
    return ((Arr) w).reverseOn(w.rank-1);
  }
  @Override public Obj callInv(Value w) {
    return call(w);
  }
  
  
  @Override public Obj call(Value a, Value w) {
    if (w instanceof Primitive) return call(w.asInt(), -1, a);
    if (w.rank+1 != a.rank) throw new RankError("(1 + ⍴⍴⍺) ≠ ⍴⍴⍵");
    int[] as = w.shape;
    int[] ws = a.shape;
    for (int i = 0; i < as.length; i++) {
      if (as[i] != ws[i]) throw new LengthError("expected shape prefixes to match");
    }
    int[] rots = w.ofShape(new int[]{w.ia}).asIntVec();
    int block = a.shape[a.rank-1];
    int cb = 0;
    if (a.quickDoubleArr()) {
      double[] vs = a.asDoubleArr();
      double[] res = new double[a.ia];
      for (int i = 0; i < rots.length; i++, cb += block) {
        int pA = rots[i];
        pA = Math.floorMod(pA, block);
        int pB = block - pA;
        System.arraycopy(vs, cb, res, cb + pB, pA);
        System.arraycopy(vs, cb + pA, res, cb, pB);
      }
      return new DoubleArr(res, a.shape);
    } else {
      Value[] vs = a.values();
      Value[] res = new Value[a.ia];
      for (int i = 0; i < rots.length; i++, cb += block) {
        int pA = rots[i];
        pA = Math.floorMod(pA, block);
        int pB = block - pA;
        System.arraycopy(vs, cb, res, cb + pB, pA);
        System.arraycopy(vs, cb + pA, res, cb, pB);
      }
      return Arr.create(res, a.shape);
    }
  }
  
  @Override public Obj call(Value a, Value w, int dim) {
    if (a instanceof Primitive) return call(a.asInt(), dim, w);
    throw new DomainError("A⌽[n]B not implemented for non-scalar A");
  }
  
  @Override public Obj callInvW(Value a, Value w) {
    return call(numM(MinusBuiltin.NF, a), w);
  }
  
  
  
  public static Value call(int a, int dim, Value w) {
    if (dim < 0) dim += w.rank;
    int rowsz = w.shape[dim];
    a = Math.floorMod(a, rowsz);
    int block = w.ia; // parts to rotate; each takes 2 arraycopy calls
    for (int i = 0; i < dim; i++) {
      block /= w.shape[i];
    }
    int sub = block/rowsz; // individual rotatable items
    int pA = sub*a; // first part
    int pB = block - pA; // second part
    // System.out.println(block+" "+rowsz+" "+bam+" "+sub+" "+pA+" "+pB);
    if (w.quickDoubleArr()) {
      double[] vs = w.asDoubleArr();
      double[] res = new double[w.ia];
      for (int cb = 0; cb < w.ia; cb += block) {
        System.arraycopy(vs, cb, res, cb + pB, pA);
        System.arraycopy(vs, cb + pA, res, cb, pB);
      }
      return new DoubleArr(res, w.shape);
    } else {
      Value[] vs = w.values();
      Value[] res = new Value[w.ia];
      for (int cb = 0; cb < w.ia; cb += block) {
        System.arraycopy(vs, cb, res, cb + pB, pA);
        System.arraycopy(vs, cb + pA, res, cb, pB);
      }
      return Arr.create(res, w.shape);
    }
  }
}