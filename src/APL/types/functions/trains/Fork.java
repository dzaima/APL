package APL.types.functions.trains;

import APL.errors.DomainError;
import APL.types.*;

public class Fork extends Fun {
  private final Obj f;
  private final Fun g, h;
  public Fork(Obj f, Fun g, Fun h) {
    this.f = f;
    this.g = g;
    this.h = h;
  }
  
  public Value call(Value w) {
    Value right = h.call(w);
    Value left = !(f instanceof Fun)? (Value) f : ((Fun) f).call(w);
    return g.call(left, right);
  }
  public Value callInv(Value w) {
    if (f instanceof Fun) throw new DomainError("(f g h)A cannot be inverted", this);
    Value left = (Value) f;
    // System.out.println(f+";"+g+";"+h);
    return h.callInv(g.callInvW(left, w));
  }
  public Value call(Value a, Value w) {
    Value right = h.call(a, w);
    Value left = !(f instanceof Fun)? (Value) f : ((Fun) f).call(a, w);
    return g.call(left, right);
  }
  
  public Value callInvW(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    Value left = (Value) f;
    return h.callInvW(a, g.callInvW(left, w));
  }
  
  public Value callInvA(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    return h.callInvA(g.callInvW((Value) f, a), w);
  }
  
  public String repr() {
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