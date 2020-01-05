package APL.tokenizer.types;

import APL.tokenizer.Token;
import APL.types.BigValue;

public class BigTok extends Token {
  public final BigValue val;
  public BigTok(String line, int spos, int epos, BigValue val) {
    super(line, spos, epos);
    this.val = val;
  }
  @Override public String toRepr() {
    return source();
  }
}
