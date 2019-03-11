package APL.types.functions;

import APL.types.*;

public class DerivedDop extends Builtin {
  private final Obj aa, ww;
  private final Dop op;
  DerivedDop(Obj aa, Obj ww, Dop op) {
    this.aa = aa;
    this.ww = ww;
    this.op = op;
    token = op.token;
  }
  
  public Obj call(Value w) {
    return op.call(aa, ww, w, this);
  }
  public Obj callInv(Value w) {
    return op.callInv(aa, ww, w);
  }
  public Obj callInvW(Value a, Value w) {
    return op.callInvW(aa, ww, a, w);
  }
  public Obj call(Value a, Value w) {
    return op.call(aa, ww, a, w, this);
  }
  @Override public String repr() {
    return aa.toString()+op.repr()+ww.toString();
  }
}