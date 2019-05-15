package APL.errors;

import APL.Main;
import APL.types.*;

public class IncorrectArgsError extends APLError {
  public IncorrectArgsError(String s, Value cause){
    super(s);
    this.cause = cause;
  }
  
  public IncorrectArgsError(String s, Tokenable fun, Value cause) {
    super(s, cause);
    Main.faulty = fun;
  }
}