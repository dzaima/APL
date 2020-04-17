package APL.errors;

import APL.types.*;

public class NYIError extends APLError { // AKA LazyError
  public NYIError(String s) {
    super(s);
  }
  public NYIError(String s, Tokenable fun) {
    super(s, fun);
  }
  public NYIError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
}