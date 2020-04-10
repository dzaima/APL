package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.*;

public class UShoeBuiltin extends Builtin {
  @Override public String repr() {
    return "∩";
  }
  
  
  
  public Value call(Value a, Value w) {
    var res = new ArrayList<Value>();
    HashSet<Value> ws = new HashSet<>(Arrays.asList(w.values()));
    for (Value v : a) if (ws.contains(v)) res.add(v);
    return Arr.create(res);
  }
}