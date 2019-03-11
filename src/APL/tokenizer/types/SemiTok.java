package APL.tokenizer.types;

import APL.tokenizer.Token;

public class SemiTok extends Token {
  public SemiTok(String line, int pos) {
    super(line, pos);
  }
  
  @Override public String toRepr() {
    return ";";
  }
}
