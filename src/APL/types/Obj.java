package APL.types;

import APL.*;
import APL.errors.NYIError;
import APL.types.dimensions.Brackets;

public abstract class Obj {
  public Token token;
  
  public String repr;
  public boolean isObj() {
    return type()==Type.array || type() == Type.var;
  }
  abstract public Type type();
  public boolean equals (Obj o) {
    if (Main.debug) Main.printdbg("non-overridden equals called");
    return false;
  }
  
  public String humanType(boolean article) {
    
    if (this instanceof Arr)     return article? "an array"    : "array";
    if (this instanceof Char)    return article? "a character" : "character";
    if (this instanceof Num)     return article? "a number"    : "number";
    if (this instanceof APLMap)  return article? "a map"       : "map";
    if (this instanceof Fun)     return article? "a function"  : "function";
    if (this instanceof Null)    return article? "javanull"    : "javanull";
    if (this instanceof Brackets)return article? "brackets"    : "brackets";
    return "some type that dzaima hasn't named in Value.humanType ಠ_ಠ";
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Obj && equals((Obj) obj);
  }
  
  public String name() {
    return toString();
  }
  
  @Override
  public String toString() {
    return humanType(false);
  }
  
  @Override
  public int hashCode() {
    throw new NYIError("hash not supported for "+this);
  }
}
