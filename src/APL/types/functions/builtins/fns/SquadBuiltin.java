package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.Obj;
import APL.types.Value;
import APL.types.functions.Builtin;

public class SquadBuiltin extends Builtin {
  public SquadBuiltin(Scope sc) {
    super("⌷");
    valid = 0x010;
    this.sc = sc;
  }
  
//  public Obj call(Value w) {
//
//  }
  
  public Obj call(Value a, Value w) {
    return w.at(a.toIntArr(this), this);
  }
}