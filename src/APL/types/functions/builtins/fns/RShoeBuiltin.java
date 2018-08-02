package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;

public class RShoeBuiltin extends Builtin {
  private Scope sc;
  public RShoeBuiltin(Scope sc) {
    super("⊃");
    this.sc = sc;
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    if (w.primitive()) return w;
    else if (w.ia == 0) return w.prototype;
    else return w.arr[0];
  }
  
  public Obj call(Value a, Value w) {
    return ((Arr)w).at(a.toIntArr(), ((Value)sc.get("⎕IO")).toInt());
  }
}