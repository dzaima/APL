package APL;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.HashMap;

public class Scope {
  private HashMap<String, Obj> vars = new HashMap<>();
  private Scope parent = null;
  public boolean alphaDefined;
  public Scope() {
    vars.put("⎕IO", new Num("1"));
    vars.put("⎕COND", Main.toAPL("01", new Token(TType.str, "01",0, "'01'")));
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
    if (name.equals("⎕COND")) {
      if (! (val instanceof Arr)) throw new DomainError("setting ⎕COND to " + Main.human(val.type));
      String s = ((Arr)val).string(false);
      if (s == null) throw new DomainError("⎕COND must be set to a character vector");
      String m = s.endsWith(" ")? s.substring(0, s.length()-1) : s;
      
      if (!m.equals("01") && !m.equals(">0") && !m.equals("≠0")) {
        throw new DomainError("⎕COND must be one of '01', '>0', '≠0' optionally followed by ' ' if space should be falsy");
      }
    }
    vars.put(name, val);
    return val;
  }

  public Obj get (String name) {
    if (name.startsWith("⎕")) {
      switch (name) {
        case "⎕MILLIS": return new Num(System.currentTimeMillis() - Main.startingMillis);
        case "⎕A": return Main.alphabet;
        case "⎕L": return Main.lowercaseAlphabet;
        case "⎕LA": return Main.lowercaseAlphabet;
        case "⎕ERASE": return new Eraser();
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
  class Eraser extends Builtin {
    Eraser() {
      super("⎕ERASE");
      valid = 0x001;
    }
    
    public Obj call(Value w) {
      w.set(null);
      return w;
    }
  }
}