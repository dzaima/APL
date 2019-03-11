package APL.tokenizer.types;

import APL.tokenizer.Token;
import APL.types.Num;

public class NumTok extends Token {
  public final Num num;
  private final String raw;
  
  public NumTok(String line, int pos, String num) {
    super(line, pos);
    this.num = new Num(num);
    raw = num;
  }
  
  @Override public String toTree(String p) {
    return p+"num : " + num + "\n";
  }
  
  @Override public String toRepr() {
    return raw;
  }
}
