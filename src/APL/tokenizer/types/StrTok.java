package APL.tokenizer.types;

import APL.Main;
import APL.tokenizer.Token;
import APL.types.arrs.ChrArr;

public class StrTok extends Token {
  public final ChrArr val;
  public final String parsed;
  
  public StrTok(String line, int spos, int epos, String str) {
    super(line, spos, epos);
    parsed = str;
    this.val = Main.toAPL(str);
  }
  
  @Override public String toRepr() {
    return source();
  }
}