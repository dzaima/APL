package APL.types.arrs;

import APL.errors.DomainError;
import APL.types.*;

import java.util.Arrays;

public class Shape1Arr extends Arr {
  private static final int[] SHAPE = new int[]{1};
  private final Value item;
  
  public Shape1Arr(Value item) {
    super(SHAPE, 1, 1);
    this.item = item;
  }
  
  @Override
  public int[] asIntArr() {
    return new int[]{item.asInt()};
  }
  
  @Override
  public int asInt() {
    throw new DomainError("Using a shape 1 array as integer", this);
  }
  
  @Override
  public Value get(int i) {
    return item;
  }
  
  @Override
  public String asString() {
    if (item instanceof Char) return String.valueOf(((Char)item).chr);
    throw new DomainError("array with non-char element used as string");
  }
  
  @Override
  public Value prototype() {
    return new Shape1Arr(item.prototype());
  }
  
  @Override
  public Value ofShape(int[] sh) {
    assert ia == Arrays.stream(sh).reduce(1, (a, b) -> a*b);
    return new SingleItemArr(item, sh);
  }
  
  @Override
  public Value[] values() {
    return new Value[]{item};
  }
  
  @Override
  public boolean quickDoubleArr() {
    return item instanceof Num;
  }
  
  @Override
  public double[] asDoubleArr() {
    return new double[]{ ((Num) item).num };
  }
}
