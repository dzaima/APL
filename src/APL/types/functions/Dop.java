package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

@SuppressWarnings("UnusedParameters")
public class Dop extends Fun {
  protected Dop(String repr) {
    super(Type.dop);
    this.repr = repr;
    this.valid = 0x000;
  }
  public DerivedDop derive(Obj aa, Obj ww) {
    return new DerivedDop(repr, aa, ww, this, valid);
  }
  public Obj call(Obj aa, Obj ww) {
    throw new IncorrectArgsException(htype() + " derived dop called niladically", this);
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    throw new IncorrectArgsException(htype() + " derived dop called monadically with " + w, this, w);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    throw new IncorrectArgsException(htype() + " derived dop called dyadically with " + a + " and " + w, this, a);
  }
  public Obj callInv(Obj aa, Obj ww, Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", this, w);
  }
  public Obj callInvW(Obj aa, Obj ww, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", this, w);
  }
  public String toString() {
    return repr;
  }
}