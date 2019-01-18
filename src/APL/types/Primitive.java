package APL.types;

import APL.errors.DomainError;

import java.util.Arrays;

public abstract class Primitive extends Value {
  private static final int[] SHAPE = new int[0];
  
  public Primitive() {
    super(SHAPE, 1, 0);
  }
  
  @Override
  public final Value get(int i) {
    return this;
  }
  
  public final Value first() {
    return this;
  }
  @Override
  public int[] asIntVec() {
    throw new DomainError("using " + this.oneliner() + " as integer vector", this);
  }
  @Override
  public int asInt() {
    throw new DomainError("using " + this.oneliner() + " as integer", this);
  }
  
  @Override
  public String asString() {
    throw new DomainError("using " + this.oneliner() + " as string", this);
  }
  
  @Override
  public Value prototype() {
    throw new DomainError("getting prototype of "+this, this);
  }
  
  @Override
  public Value with(Value what, int[] where) {
    if (where.length == 0) return what;
    throw new DomainError("trying to set an item in a scalar at "+ Arrays.toString(where));
  }
  
  @Override public Value squeeze() { // primitives are already pretty squeezed
    return this;
  }
}
