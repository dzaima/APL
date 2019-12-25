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
  
  public Obj call(Value w) {
    return op.call(aa, ww, w, this);
  }
  public Obj callInv(Value w) {
    return op.callInv(aa, ww, w);
  }
  public Obj callInvW(Value a, Value w) {
    return op.callInvW(aa, ww, a, w);
  }
  public Obj callInvA(Value a, Value w) {
    return op.callInvA(aa, ww, a, w);
  }
  public Obj call(Value a, Value w) {
    return op.call(aa, ww, a, w, this);
  }
  @Override public String repr() {
    String wws = ww.toString();
    if (!(ww instanceof Arr) && wws.length() != 1) wws = "("+wws+")";
    return aa.toString()+op.repr()+wws;
  }
}