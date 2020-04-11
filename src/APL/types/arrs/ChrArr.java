package APL.types.arrs;

import APL.Main;
import APL.errors.DomainError;
import APL.types.*;

public class ChrArr extends Arr {
  public String s;
  
  public ChrArr(String s) {
    super(new int[]{s.length()}, s.length(), 1);
    this.s = s;
  }
  public ChrArr(String s, int[] sh) {
    super(sh, s.length(), sh.length);
    assert Main.enclosePrimitives || sh.length != 0;
    this.s = s;
  }
  
  
  @Override
  public int[] asIntArr() {
    throw new DomainError("using character array as integer array", this);
  }
  
  @Override
  public int asInt() {
    throw new DomainError("Using character array as integer");
  }
  
  @Override
  public Value get(int i) {
    return Char.of(s.charAt(i));
  }
  
  @Override public Value first() {
    if (ia > 0) return Char.of(s.charAt(0));
    return Char.SPACE;
  }
  @Override
  public String asString() {
    if (rank > 1) throw new DomainError("Using rank "+rank+" character array as string");
    return s;
  }
  
  public Value prototype() {
    return Char.SPACE;
  }
  public Value safePrototype() {
    return Char.SPACE;
  }
  
  @Override
  public Value ofShape(int[] sh) {
    if (sh.length == 0 && !Main.enclosePrimitives) return Char.of(s.charAt(0));
    return new ChrArr(s, sh);
  }
  
  @Override
  public Value squeeze() {
    return this;
  }
  
  @Override
  public int hashCode() {
    if (hash == 0) {
      for (char c : s.toCharArray()) {
        hash = hash*31 + c;
      }
      hash = shapeHash(hash);
    }
    return hash;
  }
}