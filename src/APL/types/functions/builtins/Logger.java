package APL.types.functions.builtins;

import APL.APL;
import APL.types.*;

public class Logger extends Value {
  public Logger() {
    super(ArrType.chr);
    setter = true;
  }
  public Obj set(Obj v) {
    APL.println((APL.debug? "[log] " : "")+v);
    return v;
  }
  public String toString() {
    return "⎕";
  }
}