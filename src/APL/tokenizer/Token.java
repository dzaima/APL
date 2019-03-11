package APL.tokenizer;

import APL.types.Tokenable;

import java.util.*;

public abstract class Token implements Tokenable {
  public String line;
  public int pos;
  protected Token(String line, int pos) {
    this.line = line;
    this.pos = pos;
  }
  
  @Override public Token getToken() {
    return this;
  }
  public String toTree(String p) {
    p+= "  ";
    return p + this.getClass().getCanonicalName() + '\n';
  }
  public abstract String toRepr();
}