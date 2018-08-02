package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;

public class Dfn extends Fun {
  private Token token;
  private Scope psc;
  Dfn(Token t, Scope sc) {
    super(Type.fn);
    token = t;
    psc = sc;
    valid = 0x011;
  }
  public Obj call(Value w) {
    Main.printdbg("dfn call", w);
    Scope sc = new Scope(psc);
    sc.set("⍺", new PlainVar("⍺", sc));
    sc.set("⍵", w);
    return Main.execLines(token, sc);
  }
  public Obj call(Value a, Value w) {
    Main.printdbg("dfn call", a, w);
    Scope sc = new Scope(psc);
    sc.set("⍺", a);
    sc.set("⍵", w);
    sc.alphaDefined = true;
    return Main.execLines(token, sc);
  }
  public String toString() {
    return token.toRepr();
  }
}