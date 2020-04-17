package APL.types.functions;

import APL.*;
import APL.errors.*;
import APL.types.*;

public abstract class Mop extends Callable {
  
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
  
  public DerivedMop derive(Obj aa) {
    return new DerivedMop(aa, this);
  }
  public Value call(Obj f, Value w, DerivedMop derv) {
    throw new IncorrectArgsError(repr()+" can't be called monadically", derv, w);
  }
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    throw new IncorrectArgsError(repr()+" can't be called dyadically", derv, a);
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
  public Value under(Obj aa, Obj o, Value w, DerivedMop derv) {
    Value v = o instanceof Fun? ((Fun) o).call(call(aa, w, derv)) : (Value) o;
    return callInv(aa, v);
  }
  public Value underW(Obj aa, Obj o, Value a, Value w, DerivedMop derv) {
    Value v = o instanceof Fun? ((Fun) o).call(call(aa, a, w, derv)) : (Value) o;
    return callInvW(aa, a, v);
  }
  public Value underA(Obj aa, Obj o, Value a, Value w, DerivedMop derv) {
    Value v = o instanceof Fun? ((Fun) o).call(call(aa, a, w, derv)) : (Value) o;
    return callInvA(aa, v, w);
  }
  
  public String toString() {
    return repr();
  }
  public abstract String repr();
  
  protected Fun isFn(Obj o) {
    if (!(o instanceof Fun)) throw new SyntaxError("⍶ of "+repr()+" must be a function", this);
    return (Fun) o;
  }
  
  // functions are equal per-object basis
  @Override public int hashCode() {
    return actualHashCode();
  }
  @Override public boolean equals(Obj o) {
    return this == o;
  }
}