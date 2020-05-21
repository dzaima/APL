package APL.tokenizer.types;

import APL.errors.DomainError;

import java.util.List;

public class BracketTok extends TokArr<LineTok> {
  public final boolean array;
  
  public BracketTok(String line, int spos, int epos, List<LineTok> tokens, boolean hasDmd) {
    super(line, spos, tokens);
    array = tokens.size()>=2 || hasDmd;
    end(epos);
    if (tokens.size()==0 && hasDmd) throw new DomainError("[⋄] is not valid syntax", this);
  }
  
  @Override public String toRepr() {
    StringBuilder s = new StringBuilder("[");
    boolean tail = false;
    for (var v : tokens) {
      if (tail) s.append(" ⋄ ");
      s.append(v.toRepr());
      tail = true;
    }
    s.append("]");
    return s.toString();
  }
  
  public String toString() {
    return "[...]";
  }
}