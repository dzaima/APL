package APL.tokenizer.types;

import APL.Main;
import APL.tokenizer.Token;
import APL.types.arrs.ChrArr;

public class StrTok extends Token {
  public final ChrArr val;
  private final String raw;
  
  public StrTok(String line, int spos, int epos, String str) {
    super(line, spos, epos);
    raw = str;

    this.val = Main.toAPL(str);
  }
  
  @Override public String toRepr() {
    return "\"" + raw.replaceAll("\"", "\"\"") + "\"";
  }
}
