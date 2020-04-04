package APL.types.functions.trains;

import APL.Type;
import APL.errors.DomainError;
import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;

public class Fork extends Fun {
  private final Obj f;
  private final Fun g, h;
  public Fork(Obj f, Fun g, Fun h) {
    this.f = f;
    this.g = g;
    this.h = h;
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
  
  public Value call(Value w) {
    var right = (Value) h.call(w);
    var left = (Value) (f instanceof Fun? ((Fun)f).call(w) : f);
    return g.call(left, right);
  }
  public Value callInv(Value w) {
    if (f instanceof Fun) throw new DomainError("(f g h)A cannot be inverted", this);
    var left = (Value) f;
    // System.out.println(f+";"+g+";"+h);
    return h.callInv(g.callInvW(left, w));
  }
  public Value call(Value a, Value w) {
    var left = (Value) (f instanceof Fun? ((Fun)f).call(a, w) : f);
    var right = (Value) h.call(a, w);
    return g.call(left, right);
  }
  
  @Override public Value callInvW(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    Value left = (Value) f;
    return h.callInvW(a, g.callInvW(left, w));
  }
  
  @Override public Value callInvA(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    return h.callInvA(g.callInvW((Value) f, a), w);
  }
  
  @Override public String repr() {
    return "("+f+" "+g+" "+h+")";
  }
  
  public Value under(Obj o, Value w) {
    if (!(f instanceof Value)) throw new DomainError("(f g h)A cannot be inverted", this);
    Value fa = (Value) f;
    return h.under(new Fun() { public String repr() { return g.repr(); }
      public Value call(Value w) {
        return g.underW(o, fa, w);
      }
    }, w);
  }
}
