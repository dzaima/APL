package APL.errors;

import APL.types.*;

public class DomainError extends APLError {
  public DomainError(String s){
    super(s);
  }
  public DomainError(String s, Tokenable causeObj) {
    super(s);
    this.cause = causeObj;
  }
  
  public static void must(boolean b, String msg) {
    if (!b) throw new DomainError(msg);
  }
}
