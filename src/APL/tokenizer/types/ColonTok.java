package APL.tokenizer.types;

import APL.tokenizer.Token;

public class ColonTok extends Token {
  public ColonTok(String line, int pos) {
    super(line, pos);
  }
  
  @Override public String toRepr() {
    return ":";
  }
}
