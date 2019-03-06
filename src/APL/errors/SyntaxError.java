package APL.errors;

import APL.Main;
import APL.types.*;

public class SyntaxError extends APLError {
  public SyntaxError(String s){
    super(s);
  }
  public SyntaxError(String s, Value cause){
    super(s);
    this.cause = cause;
  }
  public SyntaxError(String s, Tokenable cause){
    super(s);
    this.cause = cause;
  }
  
  public static void direct(String msg, Tokenable t) {
    assert t != null;
    Main.faulty = t;
    throw new SyntaxError(msg);
  }
}
