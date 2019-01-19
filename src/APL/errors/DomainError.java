package APL.errors;

import APL.types.Obj;

public class DomainError extends APLError {
  public DomainError(String s){
    super(s);
  }
  public DomainError(String s, Obj causeObj) {
    super(s);
    this.cause = causeObj;
  }
}
