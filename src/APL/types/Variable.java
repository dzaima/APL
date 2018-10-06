package APL.types;

import APL.*;

public class Variable extends Settable {
  
  private Scope sc;
  private String name;
  
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
    return v == null? "var:"+name : "var:"+v.toString();
  }
}
