package APL.tokenizer.types;

import APL.Main;
import APL.tokenizer.Token;
import APL.types.*;

public class ChrTok extends Token {
  public final Value val;
  public final String parsed;
  
  public ChrTok(String line, int spos, int epos, String str) {
    super(line, spos, epos);
    parsed = str;
    
    if (str.length() == 1) val = new Char(str);
    else val = Main.toAPL(str);
  }
  
  @Override public String toRepr() {
    return source();
  }
}
