package APL.types.functions;

import APL.*;
import APL.types.*;

public class Builtin extends Fun {
  protected Builtin(String name) {
    super(name.equals("←")? Type.set : Type.fn); // inline bc bad java
    repr = name;
  }
  public String toString() {
    return repr;
  }
}