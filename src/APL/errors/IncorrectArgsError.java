package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class IncorrectArgsError extends APLError {
  public IncorrectArgsError(String s, Value cause){
    super(s);
    this.cause = cause;
  }
  public IncorrectArgsError(String s, Obj fn){
    super(s);
  }
}