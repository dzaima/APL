package APL.errors;

import APL.Token;
import APL.types.Obj;
import APL.types.Value;

public class SyntaxError extends APLError {
  public SyntaxError (String s){
    super(s);
  }
  public SyntaxError(String s, Value cause){
    super(s);
    this.cause = cause;
  }
  public SyntaxError (String s, Obj cause){
    super(s);
    this.cause = cause;
  }
  
  public SyntaxError(String s, Token t) {
    super(s, t);
  }
}
