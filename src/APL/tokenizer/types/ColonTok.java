package APL.tokenizer.types;

import APL.tokenizer.Token;

public class ColonTok extends Token {
  public ColonTok(String line, int spos, int epos) {
    super(line, spos, epos);
  }
  
  @Override public String toRepr() {
    return ":";
  }
}