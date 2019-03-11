package APL.types;

import APL.*;
import APL.errors.NYIError;
import APL.tokenizer.Token;
import APL.types.dimensions.*;
import APL.types.functions.VarArr;

public abstract class Obj implements Tokenable {
  public Token token;
  
  public boolean isObj() {
    return type()==Type.array || type() == Type.var;
  }
  abstract public Type type();
  public boolean equals (Obj o) {
    if (Main.debug) Main.printdbg("non-overridden equals called");
    return false;
  }
  
  public String humanType(boolean article) {
    
    if (this instanceof Arr)     return article? "an array"     : "array";
    if (this instanceof Char)    return article? "a character"  : "character";
    if (this instanceof Num)     return article? "a number"     : "number";
    if (this instanceof APLMap)  return article? "a map"        : "map";
    if (this instanceof Fun)     return article? "a function"   : "function";
    if (this instanceof Null)    return article? "javanull"     : "javanull";
    if (this instanceof Brackets)return article? "brackets"     : "brackets";
    if (this instanceof VarArr)  return article? "a vararr"     : "vararr";
    if (this instanceof Variable)return article? "a variable"   : "variable";
    if (this instanceof Pick    )return article? "an array item": "array item";
    if (this instanceof APLMap.MapPointer)return article? "a map item": "map item";
    return "some type that dzaima hasn't named in Obj.humanType ಠ_ಠ (class = "+getClass()+")";
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
  
  @Override
  public Token getToken() {
    return token;
  }
}
