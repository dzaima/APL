package APL.tokenizer.types;

import APL.tokenizer.Token;

public class SetTok extends Token {
  public SetTok(String line, int pos) {
    super(line, pos);
  }
  
  @Override public String toRepr() {
    return "←";
  }
}
