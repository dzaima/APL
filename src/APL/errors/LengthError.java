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
  
  public static void must(boolean b, String msg) {
      if (!b) throw new LengthError(msg);
  }
}
