package APL.tokenizer.types;

import APL.tokenizer.Token;

public class DColonTok extends Token {
  public DColonTok(String line, int pos) {
    super(line, pos);
  }
  
  @Override public String toRepr() {
    return "::";
  }
}
