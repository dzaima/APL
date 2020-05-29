package APL.types.arrs;

import APL.errors.DomainError;
import APL.types.*;

public class Shape1Arr extends Arr {
  private static final int[] SHAPE = new int[]{1};
  private final Value item;
  
  public Shape1Arr(Value item) {
    super(SHAPE, 1, 1);
    this.item = item;
  }
  
  @Override
  public int[] asIntArrClone() {
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
    throw new DomainError("Using array containing "+item.humanType(true)+" as string", this);
  }
  
  public Value prototype() {
    return item.prototype();
  }
  public Value safePrototype() {
    return item.safePrototype();
  }
  @Override
  public Value ofShape(int[] sh) {
    assert ia == Arr.prod(sh);
    return new SingleItemArr(item, sh);
  }
  
  @Override
  public Value[] valuesCopy() {
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