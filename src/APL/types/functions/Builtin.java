package APL.types.functions;

import APL.*;
import APL.types.*;

public abstract class Builtin extends Fun {
  protected Builtin(Scope sc) {
    super(sc);
  }
  protected Builtin() {
    super(null);
  }
}