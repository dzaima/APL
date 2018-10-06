package APL.types;

public class Null extends Value {
  public static Null NULL = new Null();
  
  @Override
  public String toString() {
    return "⎕NULL";
  }
}
