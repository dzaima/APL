package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

@SuppressWarnings("UnusedParameters")
public class Mop extends Scopeable {
  public final int valid;
  
  protected Mop(String repr, int valid, Scope sc) {
    super(sc);
    this.valid = valid;
    this.repr = repr;
  }
  protected Mop(String repr, int valid) {
    super(null);
    this.valid = valid;
    this.repr = repr;
  }
  
  @Override
  public Type type() {
    return Type.mop;
  }
  
  public DerivedMop derive (Obj aa) {
    return new DerivedMop(repr, aa, this, valid);
  }
  public Obj call(Obj f) {
    throw new IncorrectArgsException(" derived mop called niladically", this);
  }
  public Obj call(Obj f, Value w) {
    throw new IncorrectArgsException(" derived mop called monadically with " + w, w);
  }
  public Obj call(Obj f, Value a, Value w) {
    throw new IncorrectArgsException(" derived mop called dyadically", a);
  }
  public Obj callInv(Obj f, Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", w);
  }
  public Obj callInvW(Obj f, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", w);
  }
  public String toString() {
    return repr;
  }
}