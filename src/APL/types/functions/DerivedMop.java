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

  public Obj call(Value w) {
    return op.call(aa, w, this);
  }
  public Obj callInv(Value w) {
    return op.callInv(aa, w);
  }
  public Obj callInvW(Value a, Value w) {
    return op.callInvW(aa, a, w);
  }
  public Obj call(Value a, Value w) {
    return op.call(aa, a, w, this);
  }
  
  @Override public String repr() {
    return aa.toString()+op.repr();
  }
}
