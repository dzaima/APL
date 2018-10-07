package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class RankError extends APLError {
  public RankError (String s, Obj fn, Value cause){
    super(s);
    this.fn = fn;
    assert fn != null  ||  cause == null;
    this.cause = cause;
  }
}