package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class IncorrectArgsException extends APLError {
  public IncorrectArgsException (String s, Obj fn, Value cause){
    super(s);
    this.fn = fn;
    assert fn != null  ||  cause == null;
    this.cause = cause;
  }
  public IncorrectArgsException (String s, Obj fn){
    super(s);
    this.fn = fn;
    assert fn != null  ||  cause == null;
  }
}