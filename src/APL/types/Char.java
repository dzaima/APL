package APL.types;

import APL.types.arrs.ChrArr;

import static APL.Main.quotestrings;

public class Char extends Primitive {
  public static final Char SPACE = new Char(' ');
  public char chr;
  public Char(char c) {
    chr = c;
  }
  public Char(String s) {
    assert(s.length() == 1);
    chr = s.charAt(0);
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
  public String oneliner(int[] ignored) {
    return "'"+chr+"'";
  }
  
  @Override
  public Value ofShape(int[] sh) {
    assert sh.length != 0;
    int ia = 1;
    for (int c : sh) ia*= c;
    StringBuilder s = new StringBuilder();
    for(int i=0;i<ia;i++) s.append(chr);
    return new ChrArr(s.toString(), sh);
  }
  
  public int compareTo(Char v) {
    return Character.compare(chr, v.chr);
  }
  
  
  @Override public boolean equals(Obj c) {
    return c instanceof Char && chr == ((Char) c).chr;
  }
  
  public String asString() {
    return String.valueOf(chr);
  }
  
  @Override
  public int hashCode() {
    return Character.hashCode(chr);
  }
  
  @Override
  public Value prototype() {
    return SPACE;
  }
}
