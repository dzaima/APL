package APL.types.functions;

import APL.types.*;
import APL.types.arrs.HArr;

public class FunArr extends Fun {
  private final Obj[] os;
  
  public FunArr(Obj[] os) {
    this.os = os;
  }
  
  @Override public String repr() {
    StringBuilder res = new StringBuilder("(");
    boolean first = true;
    for (Obj o : os) {
      if (first) first = false;
      else res.append("⋄");
      res.append(o.toString());
    }
    res.append(")");
    return res.toString();
  }
  
  @Override public Obj call(Value w) {
    Value[] vs = new Value[os.length];
    for (int i = 0; i < os.length; i++) {
      vs[i] = (Value) ((Fun) os[i]).call(w);
    }
    return Arr.create(vs);
  }
  
  @Override public Obj call(Value a, Value w) {
    Value[] vs = new Value[os.length];
    for (int i = 0; i < os.length; i++) {
      vs[i] = (Value) ((Fun) os[i]).call(a, w);
    }
    return Arr.create(vs);
  }
}
