package APL.types;

import APL.*;
import APL.errors.NYIError;

public abstract class Obj {
  public Token token;
  
  protected String repr;
  public boolean isObj() {
    return type()==Type.array || type() == Type.var;
  }
  abstract public Type type();
  public boolean equals (Obj o) {
    if (Main.debug) Main.printdbg("non-overridden equals called");
    return false;
  }
  
  public String name() {
    return toString();
  }
  
  @Override
  public int hashCode() {
    throw new NYIError("hash not supported for "+this);
  }
}
