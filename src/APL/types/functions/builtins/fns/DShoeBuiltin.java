package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.arrs.HArr;
import APL.types.functions.Builtin;

import java.util.*;

@SuppressWarnings("Convert2Diamond") // convert.py chokes if not
public class DShoeBuiltin extends Builtin {
  public DShoeBuiltin() {
    super("∪", 0x011);
  }
  
  public Obj call(Value w) {
    var res = new LinkedHashSet<Value>(Arrays.asList(w.values()));
    return new HArr(res.toArray(new Value[0]));
  }
  
  public Obj call(Value a, Value w) {
    var m = new LinkedHashSet<Value>(Arrays.asList(a.values()));
    m.addAll(Arrays.asList(w.values()));
    return new HArr(m.toArray(new Value[0]));
  }
}