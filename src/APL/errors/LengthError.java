package APL.errors;

import APL.Main;
import APL.types.*;

public class LengthError extends APLError {
  public LengthError(String s){
    super(s);
  }
  public LengthError(String s, Value cause) {
    super(s);
    this.cause = cause;
  }
  public LengthError(String s, Tokenable fun, Value cause) {
    super(s);
    this.cause = cause;
    Main.faulty = fun;
  }
  
  public static void must(boolean b, String msg) {
      if (!b) throw new LengthError(msg);
  }
}
