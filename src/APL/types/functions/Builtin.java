package APL.types.functions;

import APL.Scope;
import APL.types.Fun;

public abstract class Builtin extends Fun {
  protected Builtin(Scope sc) {
    super(sc);
  }
  protected Builtin() {
    super(null);
  }
}