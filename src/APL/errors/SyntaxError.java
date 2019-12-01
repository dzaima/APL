package APL.errors;

import APL.Main;
import APL.types.*;
import APL.types.functions.*;

public class SyntaxError extends APLError {
  public SyntaxError(String s){
    super(s);
  }
  public SyntaxError(String s, Value cause){
    super(s);
    this.cause = cause;
  }
  public SyntaxError(String s, Tokenable cause){
    super(s, cause);
  }
  public SyntaxError(String s, Tokenable cause, Fun fun){
    super(s, cause);
    Main.faulty = fun;
  }
  public SyntaxError(String s, Tokenable cause, Mop fun){
    super(s, cause);
    Main.faulty = fun;
  }
  public SyntaxError(String s, Tokenable cause, Dop fun){
    super(s, cause);
    Main.faulty = fun;
  }
  public SyntaxError(String s, Fun fun){
    super(s);
    Main.faulty = fun;
  }
  public SyntaxError(String s, Mop fun){
    super(s);
    Main.faulty = fun;
  }
  public SyntaxError(String s, Dop fun){
    super(s);
    Main.faulty = fun;
  }
  
  public static Error direct(String msg, Tokenable t) {
    assert t != null;
    Main.faulty = t;
    throw new SyntaxError(msg);
  }
  
  public static void must(boolean b, String msg) {
    if (!b) throw new SyntaxError(msg);
  }
}