package APL.errors;

import APL.Main;
import APL.types.*;

public class DomainError extends APLError {
  public DomainError(String s){
    super(s);
  }
  public DomainError(String s, Tokenable causeObj) {
    super(s);
    this.cause = causeObj;
  }
  public DomainError(String s, Fun fun) {
    super(s);
    Main.faulty = fun;
  }
  
  public DomainError(String s, Tokenable fun, Tokenable cause) {
    super(s, cause);
    Main.faulty = fun;
  }
  
  public static void must(boolean b, String msg) {
    if (!b) throw new DomainError(msg);
  }
}
