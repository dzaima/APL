package APL.types.functions;

import APL.*;
import APL.types.*;

public class Builtin extends Fun {
  protected Builtin(String repr) {
    this.repr = repr;
  }
  public String toString() {
    return repr;
  }
}