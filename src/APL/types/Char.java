package APL.types;

import static APL.Main.quotestrings;

public class Char extends Value {
  public static final Char SPACE = new Char(' ');
  public char chr;
  public Char(char c) {
    super(ArrType.chr);
    prototype = SPACE;
    chr = c;
  }
  public Char(String s) {
    super(ArrType.chr);
    assert(s.length() == 1);
    chr = s.charAt(0);
  }

  public String toString() {
    if (quotestrings) return "'"+chr+"'";
    else return String.valueOf(chr);
  }
  protected String oneliner(int[] ignored) {
    return "'"+chr+"'";
  }
}
