package APL.errors;

import APL.types.*;

public class ImplementationError extends APLError {
  public ImplementationError(String s) {
    super(s);
  }
  public ImplementationError(String s, Tokenable fun) {
    super(s, fun);
  }
  public ImplementationError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
  public ImplementationError(Throwable t) {
    super(t.getMessage());
    initCause(t);
  }
}