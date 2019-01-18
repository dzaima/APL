package APL.types.functions;

import APL.*;
import APL.types.*;

public class Builtin extends Fun {
  protected Builtin(String repr, Scope sc) {
    super(sc);
    this.repr = repr;
  }
  protected Builtin(String repr) {
    super(null);
    this.repr = repr;
  }
  public String toString() {
    return repr;
  }
}