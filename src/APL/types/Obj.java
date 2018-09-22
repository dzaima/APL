package APL.types;

import APL.*;

public abstract class Obj {
  public final Type type;
  public boolean shy = false;
  public Obj (Type t) {
    type = t;
  }
  public Token token;
  
  public String repr;
  public boolean isObj() {
    return type==Type.array || type == Type.var;
  }

  public Obj set (Obj v) {
    if (setter) {
      sc.set(varName, v);
      return v;
    } else throw new Error("no setter; "+this);
  }
  public Obj update (Obj v) {
    if (setter) {
      sc.update(varName, v);
      return v;
    } else throw new Error("no setter; "+this);
  }
  String varName;
  private Scope sc;
  public boolean setter = false;
  public Obj varData (String varName, Scope sc) {
    this.varName = varName;
    this.sc = sc;
    setter = true;
    return this;
  }
  public boolean equals (Obj o) {
    if (Main.debug) Main.printdbg("non-overriden equals called");
    return false;
  }
  
  public String name() {
    return toString();
  }
}
