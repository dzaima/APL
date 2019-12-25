package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

@SuppressWarnings("UnusedParameters")
public abstract class Mop extends Scopeable {
  
  protected Mop(Scope sc) {
    super(sc);
  }
  protected Mop() {
    super(null);
  }
  
  @Override
  public Type type() {
    return Type.mop;
  }
  
  public DerivedMop derive (Obj aa) {
    return new DerivedMop(aa, this);
  }
  public Obj call(Obj f, Value w, DerivedMop derv) {
    throw new IncorrectArgsError(repr()+" can't be called monadically", this, w);
  }
  public Obj call(Obj f, Value a, Value w, DerivedMop derv) {
    throw new IncorrectArgsError(repr()+" can't be called dyadically", this, a);
  }
  public Obj callInv(Obj f, Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", this, w);
  }
  public Obj callInvW(Obj f, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", this, w);
  }
  public Obj callInvA(Obj f, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍺", this, w);
  }
  
  public String toString() {
    return repr();
  }
  public abstract String repr();
  
  // functions are equal per-object basis
  @Override public int hashCode() {
    return actualHashCode();
  }
  @Override public boolean equals(Obj o) {
    return this == o;
  }
}