package APL.errors;

import APL.types.*;

public class IncorrectArgsError extends APLError {
  public IncorrectArgsError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
}