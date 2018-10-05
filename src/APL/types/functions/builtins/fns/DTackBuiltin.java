package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.Arr;
import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

import java.util.ArrayList;

public class DTackBuiltin extends Builtin {
  static UTackBuiltin copy = new UTackBuiltin();
  public DTackBuiltin() {
    super("⊤");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return call(Num.TWO, w);
  }
  
  public Obj callInv(Value w) {
    return UTackBuiltin.copy.call(w);
  }
  public Obj callInvW(Value a, Value w) {
    return UTackBuiltin.copy.call(a, w);
  }
  
  public Obj call(Value a, Value w) {
    if (!(a instanceof Num)) throw new DomainError("non-scalar number base not implemented", this, a);
    if (!(w instanceof Num)) throw new DomainError("non-scalar number not implemented", this, w);
    Num base = (Num) a;
    Num num = (Num) w;
    var res = new ArrayList<Value>();
    while (num.compareTo(Num.ZERO) > 0) {
      res.add(num.mod(base));
      num = num.floorDivide(base);
    }
    return new Arr(res, true);
  }
}