package APL.types;

import APL.*;

public abstract class Obj {
  public boolean shy = false;
  public Token token;
  
  public String repr;
  public boolean isObj() {
    return type()==Type.array || type() == Type.var;
  }
  abstract public Type type();
  public boolean equals (Obj o) {
    if (Main.debug) Main.printdbg("non-overriden equals called");
    return false;
  }
  
  public String name() {
    return toString();
  }
}
