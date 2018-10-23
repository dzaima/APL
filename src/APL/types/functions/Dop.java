package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

@SuppressWarnings("UnusedParameters")
public class Dop extends Scopeable {
  public final int valid;
  
  protected Dop(String repr, int valid, Scope sc) {
    super(sc);
    this.valid = valid;
    this.repr = repr;
  }
  protected Dop(String repr, int valid) {
    super(null);
    this.valid = valid;
    this.repr = repr;
  }
  
  @Override
  public Type type() {
    return Type.dop;
  }
  
  public DerivedDop derive(Obj aa, Obj ww) {
    return new DerivedDop(repr, aa, ww, this, valid);
  }
  public Obj call(Obj aa, Obj ww) {
    throw new IncorrectArgsException("derived dop called niladically", this);
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    throw new IncorrectArgsException("derived dop called monadically", this, w);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    throw new IncorrectArgsException("derived dop called dyadically", this, a);
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