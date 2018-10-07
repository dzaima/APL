package APL.types;

import APL.*;

public class Variable extends Settable {
  
  private final Scope sc;
  public final String name;
  
  public Variable(Scope sc, String name) {
    super(sc.get(name));
    this.sc = sc;
    this.name = name;
  }
  
  @Override
  public void set(Obj v) {
    sc.set(name, v);
  }
  public void update(Obj v) {
    sc.update(name, v);
  }
  
  @Override
  public String toString() {
    if (Main.debug) return v == null? "var:"+name : "var:"+v;
    return v == null? "var:"+name : v.toString();
  }
}
