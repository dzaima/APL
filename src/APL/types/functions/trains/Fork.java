package APL.types.functions.trains;

import APL.Type;
import APL.errors.DomainError;
import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;

public class Fork extends Fun {
  private final Fun g;
  private final Obj f, h;
  public Fork(Obj f, Fun g, Obj h) {
    this.f = f;
    this.g = g;
    this.h = h;
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
  
  public Obj call(Value w) {
    var right = (Value) (h instanceof Fun? ((Fun)h).call(w) : h);
    var left = (Value) (f instanceof Fun? ((Fun)f).call(w) : f);
    return g.call(left, right);
  }
  public Obj callInv(Value w) {
    if (f instanceof Fun) throw new DomainError("inverse of f g h not supported");
    var left = (Value) f;
    // System.out.println(f+";"+g+";"+h);
    return ((Fun) h).callInv((Value) g.callInvW(left, w));
  }
  public Obj call(Value a, Value w) {
    var left = (Value) (f instanceof Fun? ((Fun)f).call(a, w) : f);
    var right = (Value) (h instanceof Fun? ((Fun)h).call(a, w) : h);
    return g.call(left, right);
  }
  
  @Override public String repr() {
    return "("+f+" "+g+" "+h+")";
  }
}
