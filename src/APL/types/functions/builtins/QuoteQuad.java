package APL.types.functions.builtins;

import APL.*;
import APL.types.*;

public class QuoteQuad extends Settable {
  private final Scope sc;
  public QuoteQuad(Scope sc) {
    super(null);
    this.sc = sc;
  }
  
  public void set(Obj v) {
    Main.print(v.toString());
  }
  
  @Override
  public Obj get() {
    return Main.toAPL(Main.console.nextLine());
  }
  public Type type() {
    return Type.gettable;
  }
  
  public String toString() {
    return "⎕";
  }
}