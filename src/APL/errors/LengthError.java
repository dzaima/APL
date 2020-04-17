package APL.errors;

import APL.types.*;

public class LengthError extends APLError {
  public LengthError(String s) {
    super(s);
  }
  public LengthError(String s, Tokenable fun) {
    super(s, fun);
  }
  public LengthError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
}