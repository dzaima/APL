package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.*;
import APL.types.functions.Builtin;

public class ReverseBuiltin extends Builtin implements DimMFn, DimDFn {
  @Override public String repr() {
    return "⌽";
  }
  
  
  public Value call(Value w, int dim) {
    return ((Arr) w).reverseOn(-dim-1);
  }
  public Value call(Value w) {
    return on(w);
  }
  public static Value on(Value w) {
    if (w instanceof Primitive) return w;
    return ((Arr) w).reverseOn(w.rank-1);
  }
  public Value callInv(Value w) {
    return call(w);
  }
  
  
  public Value call(Value a, Value w) {
    if (a instanceof Primitive) return on(a.asInt(), -1, w);
    if (a.rank+1 != w.rank) throw new RankError("(1 + ⍴⍴⍺) ≠ ⍴⍴⍵", this);
    int[] as = a.shape;
    int[] ws = w.shape;
    for (int i = 0; i < as.length; i++) {
      if (as[i] != ws[i]) throw new LengthError("expected shape prefixes to match", this);
    }
    int[] rots = a.ofShape(new int[]{a.ia}).asIntVec();
    int block = w.shape[w.rank-1];
    int cb = 0;
    if (w.quickDoubleArr()) {
      double[] vs = w.asDoubleArr();
      double[] res = new double[w.ia];
      for (int i = 0; i < rots.length; i++, cb += block) {
        int pA = rots[i];
        pA = Math.floorMod(pA, block);
        int pB = block - pA;
        System.arraycopy(vs, cb, res, cb + pB, pA);
        System.arraycopy(vs, cb + pA, res, cb, pB);
      }
      return new DoubleArr(res, w.shape);
    } else {
      Value[] vs = w.values();
      Value[] res = new Value[w.ia];
      for (int i = 0; i < rots.length; i++, cb += block) {
        int pA = rots[i];
        pA = Math.floorMod(pA, block);
        int pB = block - pA;
        System.arraycopy(vs, cb, res, cb + pB, pA);
        System.arraycopy(vs, cb + pA, res, cb, pB);
      }
      return Arr.create(res, w.shape);
    }
  }
  
  @Override public Value call(Value a, Value w, DervDimFn dims) {
    int dim = dims.singleDim(w.rank);
    if (a instanceof Primitive) return on(a.asInt(), dim, w);
    throw new DomainError("A⌽[n]B not implemented for non-scalar A", this);
  }
  
  @Override public Value callInvW(Value a, Value w) {
    return call(numM(MinusBuiltin.NF, a), w);
  }
  
  
  
  public static Value on(int a, int dim, Value w) {
    if (w.ia==0) return w;
    if (a == 0) return w;
    int rowsz = w.shape[dim];
    a = Math.floorMod(a, rowsz);
    int block = w.ia; // parts to rotate; each takes 2 arraycopy calls
    for (int i = 0; i < dim; i++) {
      block/= w.shape[i];
    }
    int sub = block/rowsz; // individual rotatable items
    int pA = sub*a; // first part
    int pB = block - pA; // second part
    // System.out.println(block+" "+rowsz+" "+bam+" "+sub+" "+pA+" "+pB);
    if (w instanceof BitArr && w.rank == 1) {
      BitArr wb = (BitArr) w;
      BitArr.BA c = new BitArr.BA(wb.shape);
      c.add(wb, a, wb.ia);
      c.add(wb, 0, a);
      return c.finish();
    } else if (w.quickDoubleArr()) {
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