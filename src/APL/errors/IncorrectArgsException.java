package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class IncorrectArgsException extends APLError {
  public IncorrectArgsException(String s, Value cause){
    super(s);
    this.cause = cause;
  }
  public IncorrectArgsException (String s, Obj fn){
    super(s);
  }
}