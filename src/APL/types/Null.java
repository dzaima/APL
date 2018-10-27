package APL.types;

import APL.Type;
import APL.types.arrs.HArr;

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
    return new HArr(new Value[]{this}, sh);
  }
}
