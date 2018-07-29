package APL.types;

public class Char extends Value {
  public static final Char SPACE = new Char(' ');
  char chr;
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
    return "'"+chr+"'";
  }
}
