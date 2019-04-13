package APL.tokenizer.types;

import APL.Main;
import APL.tokenizer.Token;
import APL.types.*;

public class ChrTok extends Token {
  public final Value val;
  private final String raw;
  
  public ChrTok(String line, int spos, int epos, String str) {
    super(line, spos, epos);
    raw = str;
    
    if (str.length() == 1) val = new Char(str);
    else val = Main.toAPL(str);
  }
  
  @Override public String toRepr() {
    return "\"" + raw.replaceAll("\"", "\"\"") + "\"";
  }
}
