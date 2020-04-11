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
  
  public Value under(Obj o, Value w) {
    return op.under(aa, ww, o, w, this);
  }
  public Value underW(Obj o, Value a, Value w) {
    return op.underW(aa, ww, o, a, w, this);
  }
  public Value underA(Obj o, Value a, Value w) {
    return op.underA(aa, ww, o, a, w, this);
  }
}