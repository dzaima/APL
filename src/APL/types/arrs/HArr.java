package APL.types.arrs;

import APL.Indexer;
import APL.errors.DomainError;
import APL.types.*;

import java.util.*;
import java.util.stream.*;

public class HArr extends Arr {
  private Value[] arr;
  public HArr(Value[] v, int[] sh) {
    super(sh, v.length, sh.length);
    arr = v;
  }
  public HArr(ArrayList<Value> v) { // 1D
    this(v.toArray(new Value[0]));
  }
  public HArr(Value[] v) { // 1D
    super(new int[]{v.length}, v.length, 1);
    arr = v;
  }
  
  @Override
  public int[] asIntVec() {
    if (rank >= 2) throw new DomainError("using array of rank≥2 as vector");
    int[] res = new int[ia];
    for (int i = 0; i < arr.length; i++) {
      res[i] = arr[i].asInt();
    }
    return res;
  }
  
  @Override
  public int asInt() {
    if (rank == 0) return arr[0].asInt();
    throw new DomainError("Using array as integer");
  }
  
  @Override
  public Value get(int i) {
    return arr[i];
  }
  
  @Override
  public boolean equals(Obj o) {
    if (!(o instanceof Value)) return false;
    Value v = (Value) o;
    if (!Arrays.equals(shape, v.shape)) return false;
    assert ia == v.ia;
    return IntStream.range(0, ia).allMatch(i -> arr[i].equals(v.get(i)));
  }
  
  public String asString() {
    if (!Arrays.stream(arr).allMatch(c -> c instanceof Char)) throw new DomainError("Converting non-char array to string");
    return Arrays.stream(arr).map(Value::asString).collect(Collectors.joining());
  }
  
  @Override
  public Value prototype() {
    return null;
  }
  
  private Integer hashCode;
  @Override
  public int hashCode() {
    if (hashCode == null) {
      hashCode = Arrays.hashCode(arr);
      hashCode^= Arrays.hashCode(shape);
    }
    return hashCode;
  }
  
  @Override
  public Value[] values() {
    return arr;
  }
  public Value ofShape(int[] sh) {
    assert ia == Arrays.stream(sh).reduce(1, (a, b) -> a*b);
    return new HArr(arr, sh);
  }
  
  @Override
  public Value with(Value what, int[] where) {
    Value[] nvals = arr.clone();
    nvals[Indexer.fromShape(shape, where, 0)] = what;
    return new HArr(nvals, shape);
  }
}
