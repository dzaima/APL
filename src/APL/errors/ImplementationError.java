package APL.errors;

import APL.types.Obj;

public class ImplementationError extends APLError {
  public ImplementationError(String s){
    super(s);
  }
  public ImplementationError(String s, Obj causeObj) {
    super(s);
    this.cause = causeObj;
  }
}
