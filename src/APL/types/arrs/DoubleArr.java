package APL.types.arrs;

import APL.*;
import APL.errors.*;
import APL.types.*;

import java.util.*;

public class DoubleArr extends Arr {
  final public double[] arr;
  public DoubleArr(double[] arr, int[] sh) {
    super(sh);
    assert Main.enclosePrimitives || sh.length != 0 : "Internal: attempting to create a DoubleArr of shape ⍬";
    this.arr = arr;
  }
  public DoubleArr(double[] arr) { // 1D
    super(new int[]{arr.length});
    this.arr = arr;
  }
  public DoubleArr(Integer[] arr) { // 1D
    super(new int[]{arr.length});
    double[] a = new double[ia];
    for (int i = 0; i < ia; i++) {
      a[i] = arr[i];
    }
    this.arr = a;
  }
  public DoubleArr(int[] arr) { // 1D
    super(new int[]{arr.length});
    double[] a = new double[ia];
    for (int i = 0; i < ia; i++) {
      a[i] = arr[i];
    }
    this.arr = a;
  }
  public DoubleArr(byte[] arr) { // 1D
    super(new int[]{arr.length});
    double[] a = new double[ia];
    for (int i = 0; i < ia; i++) {
      a[i] = arr[i];
    }
    this.arr = a;
  }
  
  public DoubleArr(ArrayList<Double> arr) {
    super(new int[]{arr.size()});
    double[] a = new double[ia];
    for (int i = 0; i < a.length; i++) {
      a[i] = arr.get(i);
    }
    this.arr = a;
  }
  
  public static Value safe(double[] vs, int[] sh) {
    if (sh.length == 0) return new Num(vs[0]);
    return new DoubleArr(vs, sh);
  }
  
  @Override
  public int[] asIntArr() {
    int[] r = new int[ia];
    for (int i = 0; i < ia; i++) {
      int conv = (int) arr[i];
      if (arr[i] != conv) throw new DomainError("using a fractional number as integer", this);
      r[i] = conv;
    }
    return r;
  }
  
  @Override
  public int asInt() {
    throw new RankError("Using a number array as integer", this);
  }
  
  @Override
  public Value get(int i) {
    return new Num(arr[i]);
  }
  
  @Override public Value first() {
    if (ia > 0) return new Num(arr[0]);
    return Num.ZERO;
  }
  
  @Override
  public String asString() {
    throw new DomainError("using double array as string", this);
  }
  
  public Value prototype() {
    return Num.ZERO;
  }
  public Value safePrototype() {
    return Num.ZERO;
  }
  
  @Override
  public Value ofShape(int[] sh) {
    assert ia == Arr.prod(sh);
    if (sh.length == 0 && !Main.enclosePrimitives) return new Num(arr[0]);
    return new DoubleArr(arr, sh);
  }
  
  @Override
  public double sum() {
   double r = 0;
   for (double val : arr) r += val;
   return r;
  }
  
  @Override
  public double[] asDoubleArr() {
    return arr;
  }
  @Override
  public double[] asDoubleArrClone() {
    return arr.clone();
  }
  
  @Override
  public boolean quickDoubleArr() {
    return true;
  }
  
  @Override
  public Value squeeze() {
    return this;
  }
  
  @Override
  public Value[] valuesCopy() {
    Value[] vs = new Value[ia];
    for (int i = 0; i < ia; i++) vs[i] = new Num(arr[i]);
    return vs;
  }
  
  public Arr reverseOn(int dim) {
    if (rank == 0) {
      if (dim != 0) throw new DomainError("rotating a scalar with a non-⎕IO axis");
      return this;
    }
    if (dim < 0) dim+= rank;
    // 2×3×4:
    // 0 - 3×4s for 2
    // 1 - 4s for 3
    // 2 - 1s for 4
    int chunkS = 1;
    int cPSec = shape[dim]; // chunks per section
    for (int i = rank-1; i > dim; i--) {
      chunkS*= shape[i];
    }
    int sec = chunkS * cPSec; // section length
    double[] res = new double[ia];
    int c = 0;
    while (c < ia) {
      for (int i = 0; i < cPSec; i++) {
        for (int j = 0; j < chunkS; j++) {
          res[c + (cPSec-i-1)*chunkS + j] = arr[c + i*chunkS + j];
        }
      }
      c+= sec;
    }
    return new DoubleArr(res, shape);
  }
  
  @Override
  public Value with(Value what, int[] where) {
    if (what instanceof Num) {
      double[] da = arr.clone();
      da[Indexer.fromShape(shape, where, 0)] = ((Num) what).num;
      return new DoubleArr(da, shape);
    }
    return super.with(what, where);
  }
  
  @Override
  public boolean equals(Obj o) {
    if (o instanceof DoubleArr) {
      DoubleArr da = (DoubleArr) o;
      if ((hash!=0 && da.hash!=0 && hash != da.hash) || !Arrays.equals(shape, da.shape)) return false;
      for (int i = 0; i < ia; i++) {
        if (arr[i] != da.arr[i]) return false;
      }
      return true;
    }
    return super.equals(o);
  }
  @Override public int hashCode() {
    if (hash == 0) {
      for (double d : arr) {
        hash = hash*31;
        if (d != 0d) hash += Double.hashCode(d); // ¯0 == 0
      }
      hash = shapeHash(hash);
    }
    return hash;
  }
}
