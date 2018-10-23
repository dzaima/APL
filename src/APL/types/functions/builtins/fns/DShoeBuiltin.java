package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;

public class DShoeBuiltin extends Builtin {
  public DShoeBuiltin() {
    super("∪", 0x011);
  }
  
  public Obj call(Value w) {
    var res = new LinkedHashSet<Value>(Arrays.asList(w.arr));
    return new Arr(res.toArray(new Value[0]));
  }
  
  public Obj call(Value a, Value w) {
    var m = new LinkedHashSet<Value>(Arrays.asList(a.arr));
    m.addAll(Arrays.asList(w.arr));
    return new Arr(m.toArray(new Value[0]));
  }
}