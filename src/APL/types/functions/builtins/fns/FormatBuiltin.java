package APL.types.functions.builtins.fns;

import APL.Main;
import APL.types.Value;
import APL.types.arrs.ChrArr;
import APL.types.functions.Builtin;

public class FormatBuiltin extends Builtin {
  @Override public String repr() {
    return "⍕";
  }
  
  
  
  public Value call(Value w) {
    if (w.rank == 1) {
      w = w.squeeze();
      if (w instanceof ChrArr) return Main.toAPL(w.asString());
    }
    return Main.toAPL(w.toString());
  }
}