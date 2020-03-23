package APL.types.functions;

import APL.types.*;

public class DerivedMop extends Fun {
  private final Obj aa;
  private final Mop op;
  DerivedMop(Obj aa, Mop op) {
    this.aa = aa;
    this.op = op;
    token = op.token;
  }
  
  public Value call(Value w) {
    return op.call(aa, w, this);
  }
  public Value call(Value a, Value w) {
    return op.call(aa, a, w, this);
  }
  public Obj callObj(Value w) {
    return op.callObj(aa, w, this);
  }
  public Obj callObj(Value a, Value w) {
    return op.callObj(aa, a, w, this);
  }
  public Value callInv(Value w) {
    return op.callInv(aa, w);
  }
  public Value callInvW(Value a, Value w) {
    return op.callInvW(aa, a, w);
  }
  public Value callInvA(Value a, Value w) {
    return op.callInvA(aa, a, w);
  }
  
  @Override public String repr() {
    return aa.toString()+op.repr();
  }
  
  public boolean strInv() { return op.strInv(aa); }
  public boolean strInvW() { return op.strInvW(aa); }
  
  public Value strInv(Value w, Value origW) { return op.strInv(aa, w, origW); }
  public Value strInvW(Value a, Value w, Value origW) { return op.strInvW(aa, a, w, origW); }
}
