package APL;


import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;

import java.util.*;

public final class Indexer implements Iterable<int[]>, Iterator<int[]> {
  private final int[] shape;
  private final int rank;
  private final int[] c;
  private int ia = 1;
  private int ci = 0;
  private final int[] offsets;
  
  public Indexer(int[] sh, int[] offsets) {
    shape = sh;
    rank = sh.length;
    c = new int[sh.length];
    this.offsets = offsets;
    for (int i = 0; i < sh.length; i++) {
      ia*= sh[i];
      c[i] = offsets[i];
    }
  }
  public Indexer(int[] sh, int IO) {
    shape = sh;
    rank = sh.length;
    c = new int[sh.length];
    this.offsets = new int[sh.length];
    for (int i = 0; i < sh.length; i++) {
      ia*= sh[i];
      offsets[i] = IO;
      c[i] = offsets[i];
    }
  }
  
  public int pos() {
    return ci-1;
  }
  
  public boolean hasNext() {
    return ci < ia;
  }
  public int[] next() {
    if (ci > 0) {
      c[rank - 1]++;
      int dim = rank - 1;
      while (c[dim] == shape[dim]+offsets[dim]) {
        if (dim == 0) break;
        c[dim] = offsets[dim];
        c[dim - 1]++;
        dim--;
      }
    }
    ci++;
    return c;
  }
  
  public static int[] add(int[] a, int b) {
    int[] res = new int[a.length];
    for (int i = 0; i < res.length; i++) res[i] = a[i] + b;
    return res;
  }
  public static int[] sub(int[] a, int b) {
    int[] res = new int[a.length];
    for (int i = 0; i < res.length; i++) res[i] = a[i] - b;
    return res;
  }
  
  public static int[] sub(int[] a, int[] b) {
    int[] res = new int[a.length];
    for (int i = 0; i < res.length; i++) res[i] = a[i] - b[i];
    return res;
  }
  public static int[] add(int[] a, int[] b) {
    int[] res = new int[a.length];
    for (int i = 0; i < res.length; i++) res[i] = a[i] + b[i];
    return res;
  }
  
  public static int fromShape(int[] shape, int[] pos, int IO) {
    int x = 0;
    for (int i = 0; i < shape.length; i++) {
      x+= pos[i] - IO;
      if (i != shape.length-1) x*= shape[i+1];
    }
    return x;
  }
  public static int fromShapeChk(int[] sh, int[] pos, Callable blame) { // IO≡0
    if (sh.length != pos.length) throw new RankError(blame+": indexing at wrong rank (shape ≡ "+Main.formatAPL(sh)+"; pos ≡ "+Main.formatAPL(pos)+")", blame);
    int x = 0;
    for (int i = 0; i < sh.length; i++) {
      x+= pos[i];
      if (pos[i]<0 || pos[i]>=sh[i]) throw new LengthError(blame+": indexing out-of-bounds (shape ≡ "+Main.formatAPL(sh)+"; pos ≡ "+Main.formatAPL(pos)+"+⎕IO)", blame);
      if (i != sh.length-1) x*= sh[i+1];
    }
    return x;
  }
  public static int fromShapeChk(int[] sh, Value pos, int IO, Callable blame) {
    if (pos.rank > 1) throw new DomainError(blame+": index rank should be ≤1 (shape ≡ "+ Main.formatAPL(pos.shape)+")", blame);
    if (sh.length != pos.ia) throw new RankError(blame+": indexing at wrong rank (shape ≡ "+Main.formatAPL(sh)+"; pos ≡ "+pos+")", blame);
    int x = 0;
    double[] ds = pos.asDoubleArr();
    for (int i = 0; i < sh.length; i++) {
      int c = (int) ds[i];
      c-= IO;
      x+= c;
      if (c<0 || c>=sh[i]) throw new LengthError(blame+": indexing out-of-bounds (shape ≡ "+Main.formatAPL(sh)+"; pos ≡ "+pos+")", blame);
      if (i != sh.length-1) x*= sh[i+1];
    }
    return x;
  }
  
  public static class PosSh { // multiple results ._.
    public final int[] vals;
    public final int[] sh;
    public PosSh(int[] vals, int[] sh) {
      this.vals = vals;
      this.sh = sh;
    }
  }
  
  // checks for rank & bound errors
  // ⎕VI←1 and sh.length≡1 allows for a shortcut of items (1 2 3 ←→ ⊂1 2 3)
  public static PosSh poss(Value v, int[] ish, int IO, Callable blame) {
    // if (v instanceof Primitive) return new PosSh(new int[]{v.asInt()-IO}, Rank0Arr.SHAPE);
    if (Main.vind) { // ⎕VI←1
      boolean deep = false;
      int[] rsh = null;
      if (!(v instanceof DoubleArr || v instanceof ChrArr || v instanceof BitArr)) {
        for (Value c : v) {
          if (!(c instanceof Primitive)) {
            if (!deep) {
              rsh = c.shape;
              deep = true;
            } else Arr.eqShapes(c.shape, rsh, blame);
          }
        }
      }
      if (v.rank > 1) throw new RankError(blame+": rank of indices must be 1 (shape ≡ "+Main.formatAPL(v.shape)+")", blame);
      if (!(!deep && ish.length==1) && ish.length!=v.ia) throw new LengthError(blame+": amount of index parts should equal rank ("+v.ia+" index parts, shape ≡ "+Main.formatAPL(ish)+")", blame);
      if (!deep) { // either the rank==1 case or a single position
        int[] res = intsNoIO(v, IO);
        if (ish.length == 1) return new PosSh(res, new int[]{res.length});
        return new PosSh(new int[]{fromShapeChk(ish, res, blame)}, Rank0Arr.SHAPE);
      }
    
      int[] res = new int[Arr.prod(rsh)];
      for (int i = 0; i < v.ia; i++) {
        Value c = v.get(i);
        if (c instanceof Primitive) {
          int n = c.asInt()-IO;
          if (n<0 || n>=ish[i]) throw new LengthError(blame+": indexing out-of-bounds (shape ≡ "+Main.formatAPL(ish)+"; pos["+(i+IO)+"] ≡ "+c+")", blame);
          for (int j = 0; j < res.length; j++) res[j]+= n;
        } else {
          double[] ns = c.asDoubleArr();
          for (int j = 0; j < ns.length; j++) {
            int n = Num.toInt(ns[j]);
            n-= IO;
            res[j]+= n;
            if (n<0 || n>=ish[i]) throw new LengthError(blame+": indexing out-of-bounds (shape ≡ "+Main.formatAPL(ish)+"; pos["+(i+IO)+"] ≡ "+n+")", blame);
          }
        }
        if (i != v.ia-1) {
          for (int j = 0; j < res.length; j++) res[j]*= ish[i+1];
        }
      }
      return new PosSh(res, rsh);
    } else { // ⎕VI←0
      int[] rsh = v.shape;
      if (v.quickDoubleArr()) {
        if (v.rank != 1) throw new RankError(blame+": indexing at rank 1, while shape was "+Main.formatAPL(v.shape), blame);
        int[] res = intsNoIO(v, IO);
        for (int c : res) if (c<0 || c>=rsh[0]) throw new LengthError(blame+": indexing out-of bounds (shape ≡ "+rsh[0]+"; pos ≡ " + (c+IO) + ")", blame);
        return new PosSh(res, rsh);
      }
      
      int[] res = new int[v.ia];
      for (int i = 0; i < v.ia; i++) res[i] = fromShapeChk(ish, v.get(i), IO, blame);
      return new PosSh(res, rsh);
    }
  }
  
  static int[] intsNoIO(Value v, int IO) {
    if (IO==0) return v.asIntArr();
    int[] res = v.asIntArrClone();
    for (int i = 0; i < res.length; i++) res[i]--;
    return res;
  }
  
  public Iterator<int[]> iterator() {
    return this;
  }
}