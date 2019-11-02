//package APL.types;
//
//import APL.types.arrs.SingleItemArr;
//
//import java.util.Arrays;
//
//public class Symbol extends Primitive {
//  private final String text;
//
//  Symbol(String text) {
//    this.text = text;
//  }
//  @Override public Value ofShape(int[] sh) {
//    assert ia == Arr.prod(sh);
//    return new SingleItemArr(this, sh);
//  }
//
//  public String toString() {
//    return "`" + text;
//  }
//}
