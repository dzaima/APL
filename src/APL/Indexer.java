package APL;


import java.util.Iterator;

public class Indexer implements Iterable<int[]>, Iterator<int[]> {
  private final int[] shape;
  private final int rank;
  private final int[] c;
  private int ia = 1;
  public int ci = 0;
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
  
  @SuppressWarnings("NullableProblems") // not using @NotNull for non-intelliJ compilers
  public Iterator<int[]> iterator() {
    return this;
  }
}