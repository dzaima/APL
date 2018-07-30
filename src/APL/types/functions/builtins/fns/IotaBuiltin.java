package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;

import static APL.APL.up;

public class IotaBuiltin extends Builtin {
  private Scope sc;
  public IotaBuiltin(Scope sc) {
    super("⍳");
    this.sc = sc;
    valid = 0x011;
  }
  public Obj call(Value w) {
    Value[] is = w.arr;
    if (is.length != 1) throw up; // lazy
    Value[] res = new Value[((Num)is[0]).intValue()];
    for (int i = 0; i < res.length; i++) res[i] = new Num(i+((Num)sc.get("⎕IO")).intValue());
    return new Arr(res);
  }
  public Obj call(Value a, Value w) { return vec(a, w); }
  protected Value scall(Value a, Value w) {
    return ((Num)a).minus((Num)w);
  }
}
