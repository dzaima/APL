package APL.types.functions;

import APL.*;
import APL.types.*;

public class Builtin extends Fun {
  protected Builtin(String repr, int valid, Scope sc) {
    super(valid, sc);
    this.repr = repr;
  }
  protected Builtin(String repr, int valid) {
    super(valid, null);
    this.repr = repr;
  }
  public String toString() {
    return repr;
  }
}