package APL.types.arrs;

import APL.errors.*;
import APL.errors.ImplementationError;
import APL.types.*;

public class EmptyArr extends Arr {
  public static EmptyArr SHAPE0 = new EmptyArr(new int[]{0});
  public EmptyArr(int[] sh) {
    super(sh, 0, sh.length);
  }
  
  @Override
  public int[] asIntArr() {
    if (rank >= 2) throw new DomainError("using rank≥2 array as integer vector");
    return new int[0];
  }
  
  @Override
  public int asInt() {
    throw new DomainError("using empty array as integer");
  }
  
  @Override
  public Value get(int i) {
    throw new ImplementationError("internal: using get() on empty array; Report )stack to dzaima");
  }
  
  @Override
  public String asString() {
    if (rank >= 2) throw new DomainError("using rank≥2 array as char vector");
    return "";
  }
  
  @Override
  public Value prototype() {
    return this;
  }
  
  @Override
  public Value ofShape(int[] sh) {
    return new EmptyArr(sh);
  }
}
