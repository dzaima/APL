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
    prototype = SPACE;
  }
  
  public Char upper() {
    return new Char(Character.toUpperCase(chr));
  }
  
  public Char lower() {
    return new Char(Character.toLowerCase(chr));
  }
  
  public Char swap() {
    if (Character.isUpperCase(chr)) return lower();
    if (Character.isLowerCase(chr)) return upper();
    return new Char(chr);
  }
  
  public int getCase() {
    return Character.isUpperCase(chr)? 1 : Character.isLowerCase(chr)? -1 : 0;
  }

  public String toString() {
    if (quotestrings) return "'"+chr+"'";
    else return String.valueOf(chr);
  }
  protected String oneliner(int[] ignored) {
    return "'"+chr+"'";
  }
  
  public int compareTo(Char v) {
    return Character.compare(chr, v.chr);
  }
  
  String fromAPL() {
    return String.valueOf(chr);
  }
}
