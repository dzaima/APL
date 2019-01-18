package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class UTackBuiltin extends Builtin {
  static final UTackBuiltin copy = new UTackBuiltin();
  public UTackBuiltin() {
    super("⊥");
  }
  
  public Obj call(Value w) {
    return call(Num.TWO, w);
  }
  
  public Obj callInv(Value w) {
    return DTackBuiltin.copy.call(w);
  }
  public Obj callInvW(Value a, Value w) {
    return DTackBuiltin.copy.call(a, w);
  }
  
  public Obj call(Value a, Value w) {
    Num res = Num.ZERO;
    Num base = ((Num)a);
    if (w.rank != 1) throw new DomainError("⊥ on rank "+w.rank, w);
    for (int i = 0; i < w.ia; i++) {
      res = res.times(base).plus((Num) w.get(i));
    }
    return res;
  }
}