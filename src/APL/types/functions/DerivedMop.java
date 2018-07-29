package APL.types.functions;

import APL.types.*;

public class DerivedMop extends Builtin {
  private Obj aa;
  private Mop op;
  DerivedMop(String s, Obj aa, Mop op, int valid) {
    super(s);
    this.valid = valid;
    this.aa = aa;
    this.op = op;
  }

  public Obj call(Value w) {
    return op.call(aa, w);
  }
  public Obj call(Value a, Value w) {
    return op.call(aa, a, w);
  }
  public String toString() {
    return aa.toString()+op.toString();
  }
}
