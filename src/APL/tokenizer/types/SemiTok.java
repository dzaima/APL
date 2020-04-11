package APL.tokenizer.types;

import APL.tokenizer.Token;

public class SemiTok extends Token {
  public SemiTok(String line, int spos, int epos) {
    super(line, spos, epos);
  }
  
  @Override public String toRepr() {
    return ";";
  }
}