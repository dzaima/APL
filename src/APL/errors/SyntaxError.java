package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class SyntaxError extends APLError {
  public SyntaxError (String s){
    super(s);
  }
  public SyntaxError (String s, Obj fn, Value cause){
    super(s);
    this.fn = fn;
    assert fn != null  ||  cause == null;
    this.cause = cause;
  }
}
