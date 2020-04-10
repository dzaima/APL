package APL.types;

import APL.Type;
import APL.types.arrs.SingleItemArr;

public class Null extends Primitive {
  public static final Null NULL = new Null();
  private Null() { }
  
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
    assert ia == Arr.prod(sh);
    return new SingleItemArr(this, sh);
  }
  
  @Override public int hashCode() {
    return 387678968; // random yay
  }
}
