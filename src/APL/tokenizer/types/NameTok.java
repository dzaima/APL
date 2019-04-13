package APL.tokenizer.types;

import APL.tokenizer.Token;

public class NameTok extends Token {
  public final String name;
  
  public NameTok(String line, int spos, int epos, String name) {
    super(line, spos, epos);
    this.name = name;
  }
  
  @Override public String toTree(String p) {
    return p+"name: " + name + "\n";
  }
  
  @Override public String toRepr() {
    return name;
  }
}
