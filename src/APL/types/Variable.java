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
  
  public void setAt(int[] pos, Value what) {
    Arr a = (Arr) v;
    Value[] nvals = new Value[a.ia];
    System.arraycopy(a.arr, 0, nvals, 0, a.ia);
    nvals[Indexer.fromShape(a.shape, pos, ((Value) sc.get("⎕IO")).toInt(null))] = what;
    set(new Arr(nvals, a.shape));
  }
  public Value getAt(int[] pos, Scope sc) {
    Arr a = (Arr) v;
    return a.at(pos, sc);
  }
  
  @Override
  public String toString() {
    if (Main.debug) return v == null? "var:"+name : "var:"+v;
    return v == null? "var:"+name : v.toString();
  }
}
