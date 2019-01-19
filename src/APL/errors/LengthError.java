package APL.errors;

import APL.types.Value;

public class LengthError extends APLError {
  public LengthError(String s){
    super(s);
  }
  public LengthError(String s, Value cause) {
    super(s);
    this.cause = cause;
  }
}
