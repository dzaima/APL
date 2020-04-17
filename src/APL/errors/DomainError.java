package APL.errors;

import APL.types.*;

public class DomainError extends APLError {
  public DomainError(String s) {
    super(s);
  }
  public DomainError(String s, Tokenable fun) {
    super(s, fun);
  }
  public DomainError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
}