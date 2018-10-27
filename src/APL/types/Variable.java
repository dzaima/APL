package APL.types;

import APL.*;
import APL.errors.ValueError;
import APL.types.arrs.HArr;

public class Variable extends Settable {
  
  private final Scope sc;
  public final String name;
  
  public Variable(Scope sc, String name) {
    super(sc.get(name));
    this.sc = sc;
    this.name = name;
  }
  
  public Obj get() {
    if (v == null) throw new ValueError("tying to get value of non-existing variable "+name);
    return v;
  }
  
  @Override
  public void set(Obj v) {
    sc.set(name, v);
  }
  public void update(Obj v) {
    sc.update(name, v);
  }
  
  public void setAt(int[] pos, Value what) { // pos has to be ⎕IO=0
    update(((Value) v).with(what, pos));
  }
  public Value getAt(int[] pos, Scope sc) {
    HArr a = (HArr) v;
    return a.at(pos, sc.IO);
  }
  
  @Override
  public String toString() {
    if (Main.debug) return v == null? "var:"+name : "var:"+v;
    return v == null? "var:"+name : v.toString();
  }
}
