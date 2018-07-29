package APL.types.functions.builtins;

import APL.Main;
import APL.types.*;

public class Logger extends Value {
  public Logger() {
    super(ArrType.chr);
    setter = true;
  }
  public Obj set(Obj v) {
    System.out.println((Main.debug? "[log] " : "")+v);
    return v;
  }
  public String toString() {
    return "⎕";
  }
}