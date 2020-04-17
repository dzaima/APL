package APL.errors;

import APL.types.*;

public class ValueError extends APLError {
  public ValueError(String s) {
    super(s);
  }
  public ValueError(String s, Tokenable fun) {
    super(s, fun);
  }
  public ValueError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
}