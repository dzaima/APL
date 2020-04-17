package APL.errors;

import APL.types.*;

public class RankError extends APLError {
  public RankError(String s) {
    super(s);
  }
  public RankError(String s, Tokenable fun) {
    super(s, fun);
  }
  public RankError(String s, Callable fun, Tokenable cause) {
    super(s, fun, cause);
  }
}