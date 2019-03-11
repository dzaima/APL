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
//    assert ia == Arrays.stream(sh).reduce(1, (a, b) -> a*b);
//    return new SingleItemArr(this, sh);
//  }
//
//  public String toString() {
//    return "`" + text;
//  }
//}
