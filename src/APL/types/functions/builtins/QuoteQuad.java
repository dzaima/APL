package APL.types.functions.builtins;

import APL.*;
import APL.types.*;

public class QuoteQuad extends Settable {
  private final Scope sc;
  public QuoteQuad(Scope sc) {
    super(null);
    this.sc = sc;
  }
  
  public void set(Obj v, Callable blame) {
    sc.sys.print(v.toString());
  }
  
  @Override
  public Obj get() {
    return Main.toAPL(sc.sys.input());
  }
  public Type type() {
    return Type.gettable;
  }
  
  public String toString() {
    return "⍞";
  }
}