package APL.tokenizer.types;

import java.util.ArrayList;

public class BasicLines extends TokArr<LineTok> {
  public BasicLines(String line, int pos, ArrayList<LineTok> tokens) {
    super(line, pos, tokens);
  }
  
  @Override public String toRepr() {
    StringBuilder s = new StringBuilder();
    boolean tail = false;
    for (var v : tokens) {
      if (tail) s.append("\n");
      s.append(v.toRepr());
      tail = true;
    }
    return s.toString();
  }
}
