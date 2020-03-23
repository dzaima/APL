package APL.types.functions;

import APL.errors.*;
import APL.types.*;
import APL.*;

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
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    throw new IncorrectArgsError(repr()+" can't be called monadically", this, w);
  }
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    throw new IncorrectArgsError(repr()+" can't be called dyadically", this, a);
  }
  public Obj callObj(Obj aa, Obj ww, Value w, DerivedDop derv) { // if overridden, call(aa, ww, w, derv) must be overridden too!
    return call(aa, ww, w, derv);
  }
  public Obj callObj(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) { // if overridden, call(aa, ww, a, w, derv) must be overridden too!
    return call(aa, ww, a, w, derv);
  }
  public Value callInv(Obj aa, Obj ww, Value w) {
    throw new DomainError(this+" doesn't support monadic inverting", this, w);
  }
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍵", this, w);
  }
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) {
    throw new DomainError(this+" doesn't support dyadic inverting of ⍺", this, w);
  }
  public boolean strInv(Obj aa, Obj ww) { return false; }
  public boolean strInvW(Obj aa, Obj ww) { return false; }
  public Value strInv(Obj aa, Obj ww, Value w, Value origW) { throw new IllegalStateException("calling unsupported dop.strInv(w, origW)"); }
  public Value strInvW(Obj aa, Obj ww, Value a, Value w, Value origW) { throw new IllegalStateException("calling unsupported dop.strInvW(a, w, origW)"); }
  
  public String toString() {
    return repr();
  }
  public abstract String repr();
  
  protected void isFn(Obj o, char c) {
    if (!(o instanceof Fun)) throw new SyntaxError(c+" of "+repr()+" must be a function", this);
  }
  
  // functions are equal per-object basis
  @Override public int hashCode() {
    return actualHashCode();
  }
  @Override public boolean equals(Obj o) {
    return this == o;
  }
}