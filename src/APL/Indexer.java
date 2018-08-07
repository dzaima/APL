package APL;


import java.util.Iterator;

public class Indexer implements Iterable<int[]>, Iterator<int[]> {
  private int[] shape;
  private int rank;
  private int[] c;
  private int ia = 1;
  private int ci = 0;
  private int[] offsets;
  
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
  
  @SuppressWarnings("NullableProblems") // not using @NotNull for non-intelliJ compilers
  public Iterator<int[]> iterator() {
    return this;
  }
}