package APL.tokenizer.types;

import APL.tokenizer.Token;

import java.util.List;

abstract public class TokArr<T extends Token> extends Token {
  public final List<T> tokens;
  
  TokArr(String line, int pos, List<T> tokens) {
    super(line, pos);
    this.tokens = tokens;
  }
  
  TokArr(String line, int spos, int epos, List<T> tokens) {
    super(line, spos, epos);
    this.tokens = tokens;
  }
  
  public String toTree(String p) {
    StringBuilder r = new StringBuilder();
    r.append(p).append(this.getClass().getCanonicalName());
    r.append(' ').append(spos).append('-').append(epos);
    r.append('\n');
    p+= "  ";
    for (Token t : tokens) r.append(t.toTree(p));
    return r.toString();
  }
}