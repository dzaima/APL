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
    if (f instanceof Fun) throw new DomainError("inverse of f g h not supported", this);
    var left = (Value) f;
    // System.out.println(f+";"+g+";"+h);
    return h.callInv((Value) g.callInvW(left, w));
  }
  public Value call(Value a, Value w) {
    var left = (Value) (f instanceof Fun? ((Fun)f).call(a, w) : f);
    var right = (Value) h.call(a, w);
    return g.call(left, right);
  }
  
  @Override public Value callInvW(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    Value left = (Value) f;
    return h.callInvW(a, (Value) g.callInvW(left, w));
  }
  
  @Override public Value callInvA(Value a, Value w) {
    if (f instanceof Fun) throw new DomainError("A(f g h)B cannot be inverted", this);
    return h.callInvA((Value) g.callInvW((Value) f, a), w);
  }
  
  @Override public String repr() {
    return "("+f+" "+g+" "+h+")";
  }
  
  public boolean strInv() {
    return f instanceof Value && g.strInvW() && h.strInv();
  }
  public Value strInv(Value w, Value origW) { // made by inlining new Atop(new Atop(f, g), h).strInv(w, origW) :D
    Value fA = (Value) f;
    Value gI = g.strInvW(fA, w, (Value) h.call(origW));
    return h.strInv(gI, origW);
  }
}
