package APL.types.functions;

import APL.types.*;

public class DerivedDop extends Fun {
  private final Obj aa, ww;
  private final Dop op;
  DerivedDop(Obj aa, Obj ww, Dop op) {
    this.aa = aa;
    this.ww = ww;
    this.op = op;
    token = op.token;
  }
  
  public Value call(Value w) {
    return op.call(aa, ww, w, this);
  }
  public Value call(Value a, Value w) {
    return op.call(aa, ww, a, w, this);
  }
  public Obj callObj(Value w) {
    return op.callObj(aa, ww, w, this);
  }
  public Obj callObj(Value a, Value w) {
    return op.callObj(aa, ww, a, w, this);
  }
  public Value callInv(Value w) {
    return op.callInv(aa, ww, w);
  }
  public Value callInvW(Value a, Value w) {
    return op.callInvW(aa, ww, a, w);
  }
  public Value callInvA(Value a, Value w) {
    return op.callInvA(aa, ww, a, w);
  }
  @Override public String repr() {
    String wws = ww.toString();
    if (!(ww instanceof Arr) && wws.length() != 1) wws = "("+wws+")";
    return aa.toString()+op.repr()+wws;
  }
  
  public boolean strInv() { return op.strInv(aa, ww); }
  public boolean strInvW() { return op.strInvW(aa, ww); }
  
  public Value strInv(Value w, Value origW) { return op.strInv(aa, ww, w, origW); }
  public Value strInvW(Value a, Value w, Value origW) { return op.strInvW(aa, ww, a, w, origW); }
}