package APL.types.functions.builtins;

import APL.Type;
import APL.types.*;

public class DerivedSet extends AbstractSet {
  
  private final SetBuiltin s;
  private final Fun f;
  
  public DerivedSet(SetBuiltin s, Fun f) {
    this.s = s;
    this.f = f;
  }
  
  @Override public Obj call(Obj a, Obj w, boolean update) {
    s.call(a, f.call((Value) ((Settable) a).get(), (Value) w), update);
    return w;
  }
  
  @Override public String repr() {
    return f.repr()+s.repr();
  }
}
