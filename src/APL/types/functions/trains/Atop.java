package APL.types.functions.trains;

import APL.Type;
import APL.errors.*;
import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;

public class Atop extends Fun {
  private final Obj g;
  private final Fun h;
  public Atop(Obj g, Fun h) {
    this.g = g;
    this.h = h;
  }
  
  public Value call(Value w) {
    if (g instanceof Fun) return ((Fun) g).call(h.call(w));
    return h.call((Value) g, w);
  }
//  public Obj callInvW(Value a, Value w) {
//    if (!(a instanceof Value)) throw new DomainError("");
//  }
  public Value callInv(Value w) {
//    if (g instanceof Fun) return ((Fun) g).callInv((Value) h.callInv(w));
    if (g instanceof Fun) return h.callInv(((Fun) g).callInv(w));
    return h.callInvW((Value) g, w);
  }
  public Value call(Value a, Value w) {
    if (!(g instanceof Fun)) throw new SyntaxError("dyadically calling an A f train");
    return ((Fun) g).call(h.call(a, w));
  }
  
  public Value callInvW(Value a, Value w) {
    if (!(g instanceof Fun)) throw new SyntaxError("inverting a dyadically called A f train");
    return h.callInvW(a, ((Fun) g).callInv(w));
  }
  
  public Value callInvA(Value a, Value w) {
    if (!(g instanceof Fun)) throw new SyntaxError("inverting a dyadically called A f train");
    return h.callInvA(((Fun) g).callInv(a), w);
  }
  
  public Value under(Obj o, Value w) {
    if (g instanceof Fun) {
      Fun gf = (Fun) g;
      return h.under(new Fun() { public String repr() { return gf.repr(); }
        public Value call(Value w) {
          return gf.under(o, w);
        }
      }, w);
    } else {
      return h.underW(o, (Value) g, w);
    }
  }
  
  @Override public String repr() {
    return "("+g+" "+h+")";
  }
  
  @Override public Type type() {
    return Type.fn;
  }
}
