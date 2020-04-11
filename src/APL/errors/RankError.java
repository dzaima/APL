package APL.errors;

import APL.Main;
import APL.types.*;

public class RankError extends APLError {
  public RankError(String s, Value cause) {
    super(s);
    this.cause = cause;
  }
  public RankError(String s, Tokenable fun, Value cause) {
    super(s);
    Main.faulty = fun;
    this.cause = cause;
  }
  public RankError(String s) {
    super(s);
  }
  
  public static void must(boolean b, String msg) {
    if (!b) throw new RankError(msg);
  }
}