package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

@SuppressWarnings("UnusedParameters")
public abstract class Dop extends Scopeable {
  
  protected Dop(Scope sc) {
    super(sc);
  }
  protected Dop() {
    super(null);
  }
  
  @Override
  public Type type() {
    return Type.dop;
  }
  
  public DerivedDop derive(Obj aa, Obj ww) {
    return new DerivedDop(aa, ww, this);
  }
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    throw new IncorrectArgsError("dyadic operator "+repr()+" can't be called monadically", w);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    throw new IncorrectArgsError("dyadic operator "+repr()+" can't be called dyadically", a);
  }
  public Obj callInv(Obj aa, Obj ww, Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", w);
  }
  public Obj callInvW(Obj aa, Obj ww, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", w);
  }
  
  public String toString() {
    return repr();
  }
  public abstract String repr();
}