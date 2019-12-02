package APL.tokenizer;

import APL.types.Tokenable;

public abstract class Token implements Tokenable {
  public String raw;
  public Integer spos; // incl
  public Integer epos; // excl
  protected Token(String raw, int spos) {
    this.raw = raw;
    this.spos = spos;
  }
  
  public Token(String raw, int spos, int epos) {
    this.raw = raw;
    this.spos = spos;
    this.epos = epos;
  }
  
  protected void end(int i) {
    assert epos == null;
    epos = i;
  }
  
  @Override public Token getToken() {
    return this;
  }
  public String toTree(String p) {
    p+= "  ";
    return p + this.getClass().getCanonicalName() + ' '+ spos + '-' + epos + '\n';
  }
  public abstract String toRepr();
  
  public String source() {
    return raw.substring(spos, epos);
  }
}