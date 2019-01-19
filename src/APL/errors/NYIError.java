package APL.errors;

import APL.types.Value;

public class NYIError extends APLError { // AKA LazyError
  public NYIError(String s) {
    super(s);
  }
  public NYIError(String s, Value cause) {
    super(s);
    this.cause = cause;
  }
}
