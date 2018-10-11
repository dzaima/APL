package APL.types;

import APL.Type;

public class Null extends Value {
  public static final Null NULL = new Null();
  
  
  @Override
  public String toString() {
    return "⎕NULL";
  }
  
  @Override
  public Type type() {
    return Type.nul;
  }
}
