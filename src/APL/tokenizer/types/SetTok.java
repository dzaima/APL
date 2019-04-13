package APL.tokenizer.types;

import APL.tokenizer.Token;

public class SetTok extends Token {
  public SetTok(String line, int spos, int epos) {
    super(line, spos, epos);
  }
  
  @Override public String toRepr() {
    return "←";
  }
}
