package APL;


import APL.errors.*;
import APL.types.*;
import APL.types.arrs.Shape1Arr;

import java.util.Iterator;

public class Indexer implements Iterable<int[]>, Iterator<int[]> {
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
  
  public static int ind(int[] shape, double[][] ds, int id, int IO) {
    int x = 0;
    for (int i = 0; i < shape.length; i++) {
      x+= ds[i][id] - IO;
      if (i != shape.length-1) x*= shape[i+1];
    }
    return x;
  }
  private static final double[][] EDAA = new double[0][0]; // empty double array array
  public static double[][] inds(Obj ov) { // ⎕VI←1 indexes to double[][]
    if (!(ov instanceof Value)) throw new SyntaxError("expected array for index array");
    Value v = (Value) ov;
    if (v instanceof Primitive) return new double[][]{{v.asDouble()}};
    if (v.rank != 1) throw new DomainError("rank of index array must be 1");
    if (v.ia == 0) return EDAA;
    if (v.get(0) instanceof Primitive) v = new Shape1Arr(v);
    double[][] res = new double[v.ia][];
    for (int i = 0; i < v.ia; i++) {
      res[i] = v.get(i).asDoubleArr();
    }
    return res;
  }
  private static final int[] i1 = new int[1];
  private static final int[] i0 = new int[0];
  public static int[] indsh(Obj ov) { // must be called after inds(ov)
    Value v = (Value) ov;
    if (v instanceof Primitive) return i1;
    if (v.ia == 0) return i0;
    Value fst = v.get(0);
    if (fst instanceof Primitive) return v.shape;
    return fst.shape;
  }
  
  public Iterator<int[]> iterator() {
    return this;
  }
}