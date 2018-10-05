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
  public DerivedMop derive (Obj aa) {
    return new DerivedMop(repr, aa, this, valid);
  }
  public Obj call(Obj f) {
    throw new IncorrectArgsException(htype() + " derived mop called niladically", this);
  }
  public Obj call(Obj f, Value w) {
    throw new IncorrectArgsException(htype() + " derived mop called monadically with " + w, this, w);
  }
  public Obj call(Obj f, Value a, Value w) {
    throw new IncorrectArgsException(htype() + " derived mop called dyadically", this, a);
  }
  public Obj callInv(Obj f, Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", this, w);
  }
  public Obj callInvW(Obj f, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", this, w);
  }
  public String toString() {
    return repr;
  }
}