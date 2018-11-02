package APL.errors;

import APL.types.Obj;

public class ValueError extends APLError {
  public ValueError (String s){
    super(s);
  }
  public ValueError (String s, Obj causeObj) {
    super(s);
    this.cause = causeObj;
  }
}
