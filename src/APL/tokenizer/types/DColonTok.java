package APL.tokenizer.types;

import APL.tokenizer.Token;

public class DColonTok extends Token {
  public DColonTok(String line, int spos, int epos) {
    super(line, spos, epos);
  }
  
  @Override public String toRepr() {
    return "::";
  }
}