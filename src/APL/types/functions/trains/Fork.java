package APL.types.functions.trains;

import APL.Type;
import APL.errors.DomainError;
import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;

public class Fork extends Fun {
  private final Fun g, h;
  private final Obj f;
  public Fork(Obj f, Fun g, Fun h) {
    this.f = f;
    this.g = g;
    this.h = h;
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
  
  public Obj call(Value w) {
    var right = (Value) h.call(w);
    var left = (Value) (f instanceof Fun? ((Fun)f).call(w) : f);
    return g.call(left, right);
  }
  public Obj callInv(Value w) {
    if (f instanceof Fun) throw new DomainError("inverse of f g h not supported", this);
    var left = (Value) f;
    // System.out.println(f+";"+g+";"+h);
    return h.callInv((Value) g.callInvW(left, w));
  }
  public Obj call(Value a, Value w) {
    var left = (Value) (f instanceof Fun? ((Fun)f).call(a, w) : f);
    var right = (Value) h.call(a, w);
    return g.call(left, right);
  }
  
  @Override public Obj callInvW(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    Value left = (Value) f;
    return h.callInvW(a, (Value) g.callInvW(left, w));
  }
  
  @Override public Obj callInvA(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    return h.callInvA((Value) g.callInvW((Value) f, a), w);
  }
  
  @Override public String repr() {
    return "("+f+" "+g+" "+h+")";
  }
}
