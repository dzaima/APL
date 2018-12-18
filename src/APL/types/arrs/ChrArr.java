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
  public int[] asIntVec() {
    throw new DomainError("using character vector as integer vector", this);
  }
  
  @Override
  public int asInt() {
    throw new DomainError("Using character array as integer");
  }
  
  @Override
  public Value get(int i) {
    return new Char(s.charAt(i));
  }
  
  @Override
  public String asString() {
    return s;
  }
  
  @Override
  public Value prototype() {
    return Char.SPACE;
  }
  
  @Override
  public Value ofShape(int[] sh) {
    if (sh.length == 0 && !Main.enclosePrimitives) return new Char(s.charAt(0));
    return new ChrArr(s, sh);
  }
  
//  @Override TODO finish
//  public Value with(Value what, int[] where) {
//    if (!(what instanceof Char)) return super.with(what, where);
//    String n =
//  }
  
  @Override
  public Value squeeze() {
    return this;
  }
}
