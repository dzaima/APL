package APL.types.functions.trains;

import APL.Type;
import APL.types.*;

public class Fork extends Fun {
  private final Fun f, g;
  private final Obj h;
  public Fork(Fun f, Fun g, Obj h) {
    this.f = f;
    this.g = g;
    this.h = h;
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
  
  public Value call(Value w) {
    var right = (Value) (h instanceof Fun? ((Fun)h).call(w) : h);
    var left = f.call(w);
    return g.call(left, right);
  }
  public Value call(Value a, Value w) {
    var right = (Value) (h instanceof Fun? ((Fun)h).call(w) : h);
    var left = f.call(w);
    return g.call(left, right);
  }
  // public Value callInv(Value w) {
  //   if (h instanceof Fun) throw new DomainError("inverse of f g h not supported");
  //   var left = (Value) f;
  //   // System.out.println(f+";"+g+";"+h);
  //   return ((Fun) h).callInv((Value) g.callInvW(left, w));
  // }
  //
  // @Override public Value callInvW(Value a, Value w) {
  //   if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
  //   Value left = (Value) f;
  //   return h.callInvW(a, g.callInvW(left, w));
  // }
  //
  // @Override public Value callInvA(Value a, Value w) {
  //   if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
  //   return h.callInvA(g.callInvW((Value) f, a), w);
  // }
  
  @Override public String repr() {
    return "("+f+" "+g+" "+h+")";
  }
  
  // public Value under(Obj o, Value w) {
  //   if (!(f instanceof Value)) throw new DomainError("(f g h)A cannot be inverted", this);
  //   Value fa = (Value) f;
  //   return h.under(new Fun() { public String repr() { return g.repr(); }
  //     public Value call(Value w) {
  //       return g.underW(o, fa, w);
  //     }
  //   }, w);
  // }
}