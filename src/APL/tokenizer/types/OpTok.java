package APL.tokenizer.types;

import APL.tokenizer.Token;

public class OpTok extends Token {
  public final String op;
  
  public OpTok(String line, int pos, String op) {
    super(line, pos);
    
    this.op = op;
  }
  
  @Override public String toRepr() {
    return op;
  }
}
