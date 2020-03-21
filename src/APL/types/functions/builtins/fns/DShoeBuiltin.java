package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;

@SuppressWarnings("Convert2Diamond") // convert.py chokes if not
public class DShoeBuiltin extends Builtin {
  @Override public String repr() {
    return "∪";
  }
  
  
  
  public Value call(Value w) {
    var res = new LinkedHashSet<Value>(Arrays.asList(w.values()));
    return Arr.create(res.toArray(new Value[0]));
  }
  
  public Value call(Value a, Value w) {
    var m = new LinkedHashSet<Value>(Arrays.asList(a.values()));
    m.addAll(Arrays.asList(w.values()));
    return Arr.create(m.toArray(new Value[0]));
  }
}