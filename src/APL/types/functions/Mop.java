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
    throw new IncorrectArgsException(htype() + " derived mops called niladically");
  }
  public Obj call(Obj f, Value w) {
    throw new IncorrectArgsException(htype() + " derived mops called monadically with " + w);
  }
  public Obj call(Obj f, Value a, Value w) {
    throw new IncorrectArgsException(htype() + " derived mops called dyadically with " + a + " and " + w);
  }
  public String toString() {
    return repr;
  }
}