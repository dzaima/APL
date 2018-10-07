package APL.errors;

import APL.types.Obj;

public class DomainError extends APLError {
  public DomainError (String s){
    super(s);
  }
  public DomainError (String s, Obj fn, Obj causeObj) {
    super(s);
    this.fn = fn;
    assert fn != null  ||  causeObj == null;
    this.cause = causeObj;
  }
}
