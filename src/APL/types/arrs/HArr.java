package APL.types.arrs;

import APL.Indexer;
import APL.errors.DomainError;
import APL.types.*;

import java.util.*;
import java.util.stream.*;

public class HArr extends Arr {
  private final Value[] arr;
  public HArr(Value[] v, int[] sh) {
    super(sh, v.length, sh.length);
    arr = v;
  }
  
  public HArr(ArrayList<Value> v) { // 1D
    super(new int[]{v.size()});
    arr = v.toArray(new Value[0]);
  }
  public HArr(Value[] v) { // 1D
    super(new int[]{v.length}, v.length, 1);
    arr = v;
  }
  
  public HArr(ArrayList<Value> v, int[] sh) {
    super(sh);
    arr = v.toArray(new Value[0]);
  }
  
  @Override
  public int[] asIntArr() {
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
    for (int i = 0; i < ia; i++) {
      if (!arr[i].equals(v.get(i))) return false;
    }
    return true;
  }
  
  public String asString() {
    StringBuilder r = new StringBuilder(ia);
    for (Value v : arr) {
      if (!(v instanceof Char)) throw new DomainError("Converting non-char array to string");
      r.append(((Char) v).chr);
    }
    return r.toString();
  }
  
  public Value prototype() {
    if (ia == 0) throw new DomainError("failed to get prototype", this);
    return arr[0].prototype();
  }
  public Value safePrototype() {
    if (ia == 0) return null;
    return arr[0].safePrototype();
  }
  public Value[] values() {
    return arr;
  }
  public Value[] valuesCopy() {
    return arr.clone();
  }
  public Value ofShape(int[] sh) {
    assert ia == Arr.prod(sh);
    return new HArr(arr, sh);
  }
  
  @Override
  public Value with(Value what, int[] where) {
    Value[] nvals = arr.clone();
    nvals[Indexer.fromShape(shape, where, 0)] = what;
    return Arr.create(nvals, shape);
  }
}
