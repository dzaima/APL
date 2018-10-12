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
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Obj)) return false;
    return equals((Obj) obj);
  }
  
  public String name() {
    return toString();
  }
  
  @Override
  public int hashCode() {
    throw new NYIError("hash not supported for "+this);
  }
}
