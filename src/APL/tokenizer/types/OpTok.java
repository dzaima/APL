package APL.tokenizer.types;

import APL.tokenizer.Token;

public class OpTok extends Token {
  public final String op;
  
  public OpTok(String line, int spos, int epos, String op) {
    super(line, spos, epos);
    
    this.op = op;
  }
  
  @Override public String toRepr() {
    return op;
  }
}