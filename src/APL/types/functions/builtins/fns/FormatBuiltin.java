package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.NYIError;
import APL.types.Arr;
import APL.types.Num;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class FormatBuiltin extends Builtin {
  public FormatBuiltin() {
    super("⍕");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    if (w instanceof Num) return Main.toAPL(w.toString(), w.token);
    throw new NYIError("can't format non-numbers", this, w);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}