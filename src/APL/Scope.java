package APL;

import APL.types.*;

import java.util.HashMap;

public class Scope {
  private HashMap<String, Obj> vars = new HashMap<>();
  private Scope parent = null;
  public boolean alphaDefined;
  Scope() {
    vars.put("⎕IO", new Num("1"));
  }
  public Scope(Scope p) {
    parent = p;
  }
  private Scope owner(String name) {
    if (vars.containsKey(name)) return this;
    else if (parent == null) return null;
    else return parent.owner(name);
  }

  public Obj update (String name, Obj val) { // sets wherever var already exists
    Scope sc = owner(name);
    if (sc == null) sc = this;
    sc.set(name, val);
    return val;
  }
  public Obj set (String name, Obj val) { // sets in current scope
    vars.put(name, val);
    return val;
  }

  public Obj get (String name) {
    if (name.startsWith("⎕")) {
      switch (name) {
        case "⎕MILLIS": return new Num(System.currentTimeMillis() - Main.startingMillis);
      }
    }
    Obj f = vars.get(name);
    if (f == null) {
      if (parent == null) return null;
      else return parent.get(name);
    } else {
      f.shy = false;
      return f;
    }
  }
  Obj getVar(String name) {
    Obj v = get(name);
    if (v == null) return new PlainVar(name, this);
    return v.varData(name, this); // experimental
  }
  public String toString() {
    return toString("");
  }
  public String toString(String prep) {
    StringBuilder res = new StringBuilder("{\n");
    String cp = prep+"  ";
    for (String n : vars.keySet()) res.append(cp).append(n).append(" ← ").append(get(n)).append("\n");
    if (parent != null) res.append(cp).append("parent: ").append(parent.toString(cp));
    res.append(prep).append("}\n");
    return res.toString();
  }
}