package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

@SuppressWarnings("UnusedParameters")
public class Mop extends Fun {
  protected Mop(String s) {
    super(Type.mop);
    repr = s;
    this.valid = 0x000;
  }
  public DerivedMop derive (Fun aa) {
    return new DerivedMop(repr, aa, this, valid);
  }
  public Obj call(Obj f) {
    throw new IncorrectArgsException(htype() + " derived mop called niladically", this);
  }
  public Obj call(Obj f, Value w) {
    throw new IncorrectArgsException(htype() + " derived mop called monadically with " + w, this, w);
  }
  public Obj call(Obj f, Value a, Value w) {
    throw new IncorrectArgsException(htype() + " derived mop called dyadically with " + a + " and " + w, this, a);
  }
  public String toString() {
    return repr;
  }
}