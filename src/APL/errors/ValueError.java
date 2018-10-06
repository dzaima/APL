package APL.errors;

import APL.types.Obj;
import APL.types.Value;

public class ValueError extends APLError {
  public ValueError (String s){
    super(s);
  }
  public ValueError (String s, Obj fn, Obj causeObj) {
    super(s);
    this.fn = fn;
    assert fn != null  ||  causeObj == null;
    this.cause = causeObj;
  }
}
