package APL.errors;

import APL.types.Value;

public class RankError extends APLError {
  public RankError(String s, Value cause) {
    super(s);
    this.cause = cause;
  }
}