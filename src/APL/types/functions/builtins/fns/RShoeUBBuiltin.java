package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;
import APL.types.functions.builtins.mops.EachBuiltin;

public class RShoeUBBuiltin extends Builtin {
  public RShoeUBBuiltin(Scope sc) {
    super("⊇", 0x011, sc);
  }
  
  public Obj call(Value w) {
    if (w.rank == 0) return w.arr[0];
    return w;
  }
  
  public Obj call(Value a, Value w) {
    return new EachBuiltin().derive(new SquadBuiltin(sc)).call(a, (Value)new LShoeBuiltin().call(w));
//    if (a instanceof Num) a = new Arr(a);
//    Value[] res = new Value[a.ia];
    
//    return new Arr(res);
  }
}