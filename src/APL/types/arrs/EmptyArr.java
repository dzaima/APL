package APL.types.arrs;

import APL.errors.*;
import APL.types.*;

import java.util.Arrays;

public class EmptyArr extends Arr {
  public static final EmptyArr SHAPE0Q = new EmptyArr(new int[]{0}, null);
  public static final EmptyArr SHAPE0N = new EmptyArr(new int[]{0}, Num.ZERO);
  public static final int[] SHAPE0 = new int[]{0};
  private final Value proto;
  public EmptyArr(int[] sh, Value proto) {
    super(sh, 0, sh.length);
    this.proto = proto;
  }
  
  @Override
  public int[] asIntArr() {
    return new int[0];
  }
  
  @Override
  public int asInt() {
    throw new DomainError("using empty array as integer");
  }
  
  @Override
  public Value get(int i) {
    throw new ImplementationError("internal: using get() on empty array; Report )stack to dzaima");
  }
  
  @Override
  public String asString() {
    if (rank >= 2) throw new DomainError("using rank≥2 array as char vector");
    return "";
  }
  
  public Value prototype() {
    if (proto == null) throw new DomainError("couldn't get prototype", this);
    return proto;
  }
  public Value safePrototype() {
    return proto;
  }
  
  @Override
  public Value ofShape(int[] sh) {
    assert ia == Arr.prod(sh);
    return new EmptyArr(sh, proto);
  }
  
  private static final Value[] NO_VALUES = new Value[0];
  @Override
  public Value[] valuesCopy() {
    return NO_VALUES; // safe, copy or not - doesn't matter
  }
  
  @Override
  public Value squeeze() {
    return this;
  }
}
