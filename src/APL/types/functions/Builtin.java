package APL.types.functions;

import APL.*;
import APL.types.*;

public class Builtin extends Fun {
  protected Builtin(String repr) {
    super(repr.equals("←")? Type.set : Type.fn); // inline bc bad java
    this.repr = repr;
  }
  public String toString() {
    return repr;
  }
}