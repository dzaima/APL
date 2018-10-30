package APL.types.arrs;

import APL.errors.DomainError;
import APL.types.*;

public class SingleItemArr extends Arr {
  private final Value v;
  
  public SingleItemArr(Value v, int[] shape) {
    super(shape);
    this.v = v;
  }
  
  public SingleItemArr(Value v, int[] shape, int ia, int rank) {
    super(shape, ia, rank);
    this.v = v;
  }
  
  @Override
  public int[] asIntVec() {
    int vi = v.asInt();
    int[] a = new int[ia];
    for (int i = 0; i < ia; i++) a[i] = vi;
    return a;
  }
  
  @Override
  public int asInt() {
    throw new DomainError("using array as integer", this);
  }
  
  @Override
  public Value get(int i) {
    return v;
  }
  
  @Override
  public String asString() {
    if (rank >= 2) throw new DomainError("using rank≥2 array as string");
    if (! (v instanceof Char)) throw new DomainError("using non-char array as string");
    char c = ((Char) v).chr;
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < ia; i++) s.append(c);
    return s.toString();
  }
  
  @Override
  public Value prototype() {
    return v.prototype();
  }
  
  @Override
  public Value ofShape(int[] sh) {
    return new SingleItemArr(v, sh);
  }
  
  @Override
  public boolean quickDoubleArr() {
    return v instanceof Num;
  }
  public Value[] values() {
    Value[] vs = new Value[ia];
    for (int i = 0; i < ia; i++) vs[i] = v;
    return vs;
  }
  @Override
  public double[] asDoubleArr() {
    double[] res = new double[ia];
    double n = v.asDouble();
    for (int i = 0; i < ia; i++) res[i] = n;
    return res;
  }
  
  @Override
  public double[] asDoubleArrClone() {
    return asDoubleArr();
  }
  
  @Override
  public Value squeeze() {
    Value ov = v.squeeze();
    if (ov == v) return this;
    return new SingleItemArr(v, shape);
  }
}