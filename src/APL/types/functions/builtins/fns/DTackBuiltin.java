package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;

public class DTackBuiltin extends Builtin {
  static final UTackBuiltin copy = new UTackBuiltin();
  public DTackBuiltin() {
    super("⊤", 0x011);
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
    Collections.reverse(res);
    return new Arr(res);
  }
}