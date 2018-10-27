package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.*;
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
    if (!(a instanceof Num)) throw new DomainError("non-scalar number base not implemented", a);
    if (!(w instanceof Num)) throw new DomainError("non-scalar number not implemented", w);
    double base = ((Num) a).asDouble();
    double num = ((Num) w).asDouble();
    var res = new ArrayList<Double>();
    while (num > 0) {
      res.add(num%base);
      num = Math.floor(num/base);
    }
    double[] f = new double[res.size()];
    for (int i = res.size()-1, j = 0; i >= 0; i--, j++) {
      f[j] = res.get(i);
    }
    return new DoubleArr(f);
  }
}