package APL.errors;

import APL.Main;
import APL.types.*;

public class NYIError extends APLError { // AKA LazyError
  public NYIError(String s) {
    super(s);
  }
  public NYIError(String s, Tokenable fun) {
    super(s);
    Main.faulty = fun;
  }
  public NYIError(String s, Tokenable fun, Tokenable cause) {
    super(s, cause);
    Main.faulty = fun;
  }
}
