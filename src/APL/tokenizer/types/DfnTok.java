package APL.tokenizer.types;

import java.util.ArrayList;

public class DfnTok extends TokArr<LineTok> {
  
  public DfnTok(String line, int pos, ArrayList<LineTok> lns) {
    super(line, pos, lns);
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
