package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class SemiUBBuiltin extends Builtin {
  public SemiUBBuiltin() {
    super("⍮", 0x01);
  }
  
  @Override
  public Obj call(Value w) {
    return new Shape1Arr(w);
  }
  
  public Obj call(Value a, Value w) {
    if (a instanceof Num && w instanceof Num) {
      return new DoubleArr(new double[]{((Num) a).num, ((Num) w).num});
    }
    if (a instanceof Char && w instanceof Char) {
      return new ChrArr(((Char) a).chr +""+ ((Char) w).chr);
    }
    return Arr.create(new Value[]{a, w});
  }
}