package APL.types.functions;

import APL.types.*;

public class DerivedMop extends Builtin {
  private final Obj aa;
  private final Mop op;
  DerivedMop(String s, Obj aa, Mop op, int valid) {
    super(s, valid);
    this.aa = aa;
    this.op = op;
    token = op.token;
  }

  public Obj call(Value w) {
    return op.call(aa, w);
  }
  public Obj callInv(Value w) {
    return op.callInv(aa, w);
  }
  public Obj callInvW(Value a, Value w) {
    return op.callInvW(aa, a, w);
  }
  public Obj call(Value a, Value w) {
    return op.call(aa, a, w);
  }
  public String toString() {
    return aa.toString()+op.toString();
  }
}
