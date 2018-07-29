package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

@SuppressWarnings("UnusedParameters")
public class Dop extends Fun {
  protected Dop(String s) {
    super(Type.dop);
    repr = s;
    this.valid = 0x000;
  }
  public DerivedDop derive(Obj aa, Obj ww) {
    return new DerivedDop(repr, aa, ww, this, valid);
  }
  public Obj call(Obj aa, Obj ww) {
    throw new IncorrectArgsException(htype() + " derived dop called niladically");
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    throw new IncorrectArgsException(htype() + " derived dop called monadically with " + w);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    throw new IncorrectArgsException(htype() + " derived dop called dyadically with " + a + " and " + w);
  }
  public String toString() {
    return repr;
  }
}