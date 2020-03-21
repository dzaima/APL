package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

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
  public Value call(Obj f, Value w, DerivedMop derv) {
    throw new IncorrectArgsError(repr()+" can't be called monadically", this, w);
  }
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    throw new IncorrectArgsError(repr()+" can't be called dyadically", this, a);
  }
  public Obj callObj(Obj f, Value w, DerivedMop derv) { // if overridden, call(f, w, derv) must be overridden too!
    return call(f, w, derv);
  }
  public Obj callObj(Obj f, Value a, Value w, DerivedMop derv) { // if overridden, call(f, a, w, derv) must be overridden too!
    return call(f, a, w, derv);
  }
  public Value callInv(Obj f, Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", this, w);
  }
  public Value callInvW(Obj f, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", this, w);
  }
  public Value callInvA(Obj f, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍺", this, w);
  }
  public boolean strInv(Obj f) { return false; }
  public boolean strInvW(Obj f) { return false; }
  public Value strInv(Obj f, Value w, Value origW) { throw new IllegalStateException("calling unsupported mop.strInv(w, origW)"); }
  public Value strInvW(Obj f, Value a, Value w, Value origW) { throw new IllegalStateException("calling unsupported mop.strInvW(a, w, origW)"); }
  
  public String toString() {
    return repr();
  }
  public abstract String repr();
  
  protected void isFn(Obj o) {
    if (!(o instanceof Fun)) throw new SyntaxError("⍶ of "+repr()+" must be a function", this);
  }
  
  // functions are equal per-object basis
  @Override public int hashCode() {
    return actualHashCode();
  }
  @Override public boolean equals(Obj o) {
    return this == o;
  }
}