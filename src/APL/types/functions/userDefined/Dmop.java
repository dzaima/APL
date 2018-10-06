package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

import static APL.Main.*;


public class Dmop extends Mop {
  private Token token;
  private Scope psc;
  Dmop(Token t, Scope sc) {
    super(t.toRepr());
    token = t;
    psc = sc;
    valid = 0x011;
  }
  public Obj call(Obj aa, Value w) {
    printdbg("dmop call", w);
    sc = new Scope(psc);
    sc.set("⍶", aa);
    sc.set("⍺", new Variable(sc, "⍺"));
    sc.set("⍵", w);
    return execLines(token, sc);
  }
  public Obj call(Obj aa, Value a, Value w) {
    printdbg("dmop call", a, w);
    sc = new Scope(psc);
    sc.set("⍶", aa);
    sc.set("⍺", a);
    sc.set("⍵", w);
    sc.alphaDefined = true;
    return execLines(token, sc);
  }
  public String toString() {
    return token.toRepr();
  }
}
