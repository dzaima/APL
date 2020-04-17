package APL.errors;

import APL.types.*;

public class SyntaxError extends APLError {
  public SyntaxError(String s) {
    super(s);
  }
  public SyntaxError(String s, Tokenable fun) {
    super(s, fun);
  }
  public SyntaxError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
  
  public static void must(boolean b, String msg) {
    if (!b) throw new SyntaxError(msg);
  }
}