package APL.tokenizer.types;

import APL.tokenizer.Token;

public class BacktickTok extends Token {
  private final LineTok val;
  
  public BacktickTok(String raw, int spos, int epos, Token val) {
    super(raw, spos, epos);
    this.val = LineTok.inherit(val);
  }
  
  @Override public String toRepr() {
    return "`" + val.toRepr();
  }
  
  public LineTok value() {
    return val;
  }
}
