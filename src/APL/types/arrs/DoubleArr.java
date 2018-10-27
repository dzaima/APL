package APL.types.arrs;

import APL.errors.*;
import APL.types.*;

import java.util.Arrays;

public class DoubleArr extends Arr {
  final public double[] vals;
  public DoubleArr(double[] vals, int[] sh) {
    super(sh);
    assert sh.length != 0;
    this.vals = vals;
  }
  public DoubleArr(double[] vals) { // 1D
    super(new int[]{vals.length});
    this.vals = vals;
  }
  public DoubleArr(Integer[] vals) { // 1D
    super(new int[]{vals.length});
    double[] a = new double[ia];
    for (int i = 0; i < ia; i++) {
      a[i] = vals[i];
    }
    this.vals = a;
  }
  
  public DoubleArr(double[] vals, int[] sh, int ia, int rank) {
    super(sh, ia, rank);
    assert sh.length != 0;
    this.vals = vals;
  }
  
  @Override
  public int[] asIntArr() {
    if (rank >= 2) throw new RankError("trying to use a rank "+rank+" number array as vector", this);
    int[] r = new int[ia];
    for (int i = 0; i < ia; i++) {
      if (vals[i] != (int)vals[i]) throw new DomainError("using a fractional number as integer", this);
      r[i] = ((int) vals[i]);
    }
    return r;
  }
  
  @Override
  public int asInt() {
    throw new RankError("Using a number array as integer", this);
  }
  
  @Override
  public Value get(int i) {
    return new Num(vals[i]);
  }
  
  @Override
  public String asString() {
    throw new DomainError("using double array as string", this);
  }
  
  @Override
  public Value prototype() {
    return Num.ZERO;
  }
  
  @Override
  public Value ofShape(int[] sh) {
    return new DoubleArr(vals, sh);
  }
  
  @Override
  public double sum() { // TODO whether or not commented code
//    double r = 0;
//    for (double val : vals) r += val;
//    return r;
    return Arrays.stream(vals).sum();
  }
  
  @Override
  public double[] asDoubleArr() {
    return vals;
  }
}
