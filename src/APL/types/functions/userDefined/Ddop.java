package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

import static APL.Main.*;


public class Ddop extends Dop {
  private final Token token;
  Ddop(Token t, Scope sc) {
    super(t.toRepr(), 0x011, sc);
    token = t;
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    printdbg("ddop call", w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍹", ww);
    nsc.set("⍺", new Variable(nsc, "⍺"));
    nsc.set("⍵", w);
    return execLines(token, nsc);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    printdbg("ddop call", a, w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍹", ww);
    nsc.set("⍺", a);
    nsc.set("⍵", w);
    nsc.alphaDefined = true;
    return execLines(token, nsc);
  }
  public String toString() {
    return token.toRepr();
  }
}
