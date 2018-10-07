package APL.types.functions;

import APL.types.*;

public class DerivedDop extends Builtin {
  private final Obj aa, ww;
  private final Dop op;
  DerivedDop(String s, Obj aa, Obj ww, Dop op, int valid) {
    super(s);
    this.valid = valid;
    this.aa = aa;
    this.ww = ww;
    this.op = op;
  }
  
  public Obj call(Value w) {
    return op.call(aa, ww, w);
  }
  public Obj callInv(Value w) {
    return op.callInv(aa, ww, w);
  }
  public Obj callInvW(Value a, Value w) {
    return op.callInvW(aa, ww, a, w);
  }
  public Obj call(Value a, Value w) {
    return op.call(aa, ww, a, w);
  }
  public String toString() {
    return aa.toString()+op.toString()+ww.toString();
  }
}