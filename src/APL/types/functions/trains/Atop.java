package APL.types.functions.trains;

import APL.Type;
import APL.errors.SyntaxError;
import APL.types.Fun;
import APL.types.Obj;
import APL.types.Value;

public class Atop extends Fun {
  private Obj g;
  private Fun h;
  public Atop(Obj g, Fun h) {
    super(Type.fn);
    this.g = g;
    this.h = h;
  }
  
  public Obj call(Value w) {
    if (g instanceof Fun) return ((Fun) g).call((Value) h.call(w));
    return h.call((Value) g, w);
  }
  public Obj call(Value a, Value w) {
    if (!(g instanceof Fun)) throw new SyntaxError("dyadically calling an A f train");
    return ((Fun) g).call((Value) h.call(a, w));
  }
  
  public String toString() {
    return "("+g+" "+h+")";
  }
}
