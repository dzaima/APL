package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class NYIError extends APLError { // AKA LazyError
  public NYIError (String s) {
    super(s);
  }
  public NYIError (String s, Obj fn, Value cause) {
    super(s);
    this.fn = fn;
    assert fn != null  ||  cause == null;
    this.cause = cause;
  }
}
