package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

import static APL.Main.*;


public class Ddop extends Dop {
  private final Token token;
  private final Scope psc;
  Ddop(Token t, Scope sc) {
    super(t.toRepr());
    token = t;
    psc = sc;
    valid = 0x011;
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    printdbg("ddop call", w);
    sc = new Scope(psc);
    sc.set("⍶", aa);
    sc.set("⍹", ww);
    sc.set("⍺", new Variable(sc, "⍺"));
    sc.set("⍵", w);
    return execLines(token, sc);
  }
  public Obj call(Obj aa, Obj ww, Value a, Value w) {
    printdbg("ddop call", a, w);
    sc = new Scope(psc);
    sc.set("⍶", aa);
    sc.set("⍹", ww);
    sc.set("⍺", a);
    sc.set("⍵", w);
    sc.alphaDefined = true;
    return execLines(token, sc);
  }
  public String toString() {
    return token.toRepr();
  }
}
