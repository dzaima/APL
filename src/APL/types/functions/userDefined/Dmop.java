package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

import static APL.Main.*;


public class Dmop extends Mop {
  private final Token token;
  Dmop(Token t, Scope sc) {
    super(t.toRepr(), 0x011, sc);
    token = t;
  }
  public Obj call(Obj aa, Value w) {
    printdbg("dmop call", w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍺", new Variable(nsc, "⍺"));
    nsc.set("⍵", w);
    return execLines(token, nsc);
  }
  public Obj call(Obj aa, Value a, Value w) {
    printdbg("dmop call", a, w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍺", a);
    nsc.set("⍵", w);
    nsc.alphaDefined = true;
    return execLines(token, nsc);
  }
  public String toString() {
    return token.toRepr();
  }
}
