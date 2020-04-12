package APL.types.functions;

import APL.types.*;
import APL.types.functions.builtins.mops.TableBuiltin;

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
    return op instanceof TableBuiltin? "∘."+aa.toString() : aa.toString()+op.repr();
  }
  
  public Value under(Obj o, Value w) {
    return op.under(aa, o, w, this);
  }
  public Value underW(Obj o, Value a, Value w) {
    return op.underW(aa, o, a, w, this);
  }
  public Value underA(Obj o, Value a, Value w) {
    return op.underA(aa, o, a, w, this);
  }
}