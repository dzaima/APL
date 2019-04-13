package APL.tokenizer.types;

import java.util.*;

public class DfnTok extends TokArr<LineTok> {
  
  public DfnTok(String line, int spos, int epos, List<LineTok> tokens) {
    super(line, spos, tokens);
    end(epos);
  }
  
  @Override public String toRepr() {
    StringBuilder s = new StringBuilder("{");
    boolean tail = false;
    for (var v : tokens) {
      if (tail) s.append(" ⋄ ");
      s.append(v.toRepr());
      tail = true;
    }
    s.append("}");
    return s.toString();
  }
}
