package APL.types;


import APL.Scope;

public class PlainVar extends Value {
  private Scope sc;
  private String varName;
  public PlainVar(String name, Scope sc) {
    super(ArrType.nothing);
    varName = name;
    this.sc = sc;
    setter = true;
  }
  public Obj set   (Obj v) { return sc.set   (varName, v); }
  public Obj update(Obj v) { return sc.update(varName, v); }
  public String toString() {
    return "var:" + varName;
  }
}