package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class DomainError extends APLError {
  public DomainError (String s){
    super(s);
  }
  public DomainError (String s, Obj fn, Value cause) {
    super(s);
    this.fn = fn;
    assert fn != null  ||  cause == null;
    this.cause = cause;
  }
}
