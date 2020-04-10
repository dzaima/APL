package APL.types;

import APL.Main;
import APL.types.arrs.ChrArr;

import static APL.Main.quotestrings;

public class Char extends Primitive {
  public char chr;
  public static final Char[] ASCII;
  static {
    ASCII = new Char[128];
    for (int i = 0; i < 128; i++) {
      ASCII[i] = new Char((char) i);
    }
  }
  
  public static final Char SPACE = ASCII[' '];
  
  public Char(char c) {
    chr = c;
  }
  
  public static Char of(char c) {
    if (c < 128) return ASCII[c];
    return new Char(c);
  }
  
  public Char upper() {
    return Char.of(Character.toUpperCase(chr));
  }
  
  public Char lower() {
    return Char.of(Character.toLowerCase(chr));
  }
  
  public Char swap() {
    if (Character.isUpperCase(chr)) return lower();
    if (Character.isLowerCase(chr)) return upper();
    return Char.of(chr);
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
    if (sh.length == 0 && !Main.enclosePrimitives) return this;
    assert ia == Arr.prod(sh);
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < ia; i++) s.append(chr);
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
    return chr;
  }
  
  public Value safePrototype() {
    return SPACE;
  }
}
