package APL.types.functions.builtins;

import APL.*;
import APL.types.*;

public class Quad extends Settable {
  private final Scope sc;
  public Quad(Scope sc) {
    super(null);
    this.sc = sc;
  }
  
  public void set(Obj v, Callable blame) {
    sc.sys.println((Main.debug? "[log] " : "")+v);
  }
  
  @Override
  public Obj get() {
    return Main.exec(sc.sys.input(), sc);
  }
  public Type type() {
    return Type.gettable;
  }
  
  public String toString() {
    return "⎕";
  }
}