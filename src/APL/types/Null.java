package APL.types;

import APL.Type;
import APL.types.arrs.SingleItemArr;

import java.util.Arrays;

public class Null extends Primitive {
  public static final Null NULL = new Null();
  
  
  @Override
  public String toString() {
    return "⎕NULL";
  }
  
  @Override
  public Type type() {
    return Type.nul;
  }
  
  @Override
  public Value ofShape(int[] sh) {
    assert ia == Arrays.stream(sh).reduce(1, (a, b) -> a*b);
    return new SingleItemArr(this, sh);
  }
}
