package APL.tokenizer.types;

import java.util.ArrayList;

public class BracketTok extends TokArr<LineTok> {
  
  public BracketTok(String line, int pos, ArrayList<LineTok> tokens) {
    super(line, pos, tokens);
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
}
