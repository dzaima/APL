package APL.tokenizer.types;

import java.util.List;

public class ParenTok extends TokArr<LineTok> {
  public final boolean hasDmd;
  
  public ParenTok(String line, int spos, int epos, List<LineTok> tokens, boolean hasDmd) {
    super(line, spos, tokens);
    this.hasDmd = hasDmd;
    end(epos);
  }
  
  @Override public String toRepr() {
    StringBuilder s = new StringBuilder("(");
    boolean tail = false;
    for (var v : tokens) {
      if (tail) s.append(" ⋄ ");
      s.append(v.toRepr());
      tail = true;
    }
    s.append(")");
    return s.toString();
  }
}