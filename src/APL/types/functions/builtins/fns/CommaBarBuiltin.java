package APL.types.functions.builtins.fns;

import APL.types.Value;
import APL.types.functions.Builtin;

public class CommaBarBuiltin extends Builtin {
  @Override public String repr() {
    return "⍪";
  }
  
  
  
  public Value call(Value w) {
    if (w.rank==1) return w.ofShape(new int[]{w.shape[0], 1});
    if (w.rank==0) return w.ofShape(new int[]{1, 1});
    int tsz = 1;
    for (int i = 1; i < w.shape.length; i++) tsz*= w.shape[i];
    return w.ofShape(new int[]{w.shape[0], tsz});
  }
  
  public Value call(Value a, Value w) {
    return CatBuiltin.cat(a, w, 0, this);
  }
}