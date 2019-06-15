package APL.tokenizer.types;

import APL.tokenizer.Token;

public class ErrTok extends Token {
  public ErrTok(String raw, int spos, int epos) {
    super(raw, spos, epos);
  }
  public ErrTok(String raw, int onepos) {
    super(raw, onepos, onepos+1);
  }
  
  @Override public String toRepr() {
    return null;
  }
}
