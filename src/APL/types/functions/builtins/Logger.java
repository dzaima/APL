package APL.types.functions.builtins;

import APL.*;
import APL.types.*;

public class Logger extends Settable {
  private Scope sc;
  public Logger(Scope sc) {
    super(null);
    this.sc = sc;
  }
  
  public void set(Obj v) {
    Main.println((Main.debug? "[log] " : "")+v);
  }
  
  @Override
  public Obj get() {
    return Main.exec(Main.console.nextLine(), sc);
  }
  
  public String toString() {
    return "⎕";
  }
}