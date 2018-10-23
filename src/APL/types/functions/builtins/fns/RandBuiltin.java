package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.NYIError;
import APL.types.*;
import APL.types.functions.Builtin;

public class RandBuiltin extends Builtin {
  public RandBuiltin(Scope sc) {
    super("?", 0x011, sc);
  }
  
  public Obj call(Value w) {
    return scalar(v -> {
      Num n = (Num) v;
      if (n.equals(Num.ZERO)) return new Num(sc.rand(1d));
      else return new Num(sc.rand(n.intValue()) + ((Num)sc.get("⎕IO")).intValue());
    }, w);
  }
  
  public Obj call(Value a, Value w) {
    throw new NYIError("sorry", this, a);
  }
}