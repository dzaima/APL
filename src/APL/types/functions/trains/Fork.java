package APL.types.functions.trains;

import APL.Type;
import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;

public class Fork extends Fun {
  private Fun g;
  private Obj f, h;
  public Fork(Obj f, Fun g, Obj h) {
    super(Type.fn);
    this.f = f;
    this.g = g;
    this.h = h;
  }
  
  public Obj call(Value w) {
    var left = (Value) (f instanceof Fun? ((Fun)f).call(w) : f);
    var right = (Value) (h instanceof Fun? ((Fun)h).call(w) : h);
    return g.call(left, right);
  }
  public Obj call(Value a, Value w) {
    var left = (Value) (f instanceof Fun? ((Fun)f).call(a, w) : f);
    var right = (Value) (h instanceof Fun? ((Fun)h).call(a, w) : h);
    return g.call(left, right);
  }
  
  public String toString() {
    return "("+f+" "+g+" "+h+")";
  }
}
