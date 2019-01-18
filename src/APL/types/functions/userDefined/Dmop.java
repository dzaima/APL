package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

import static APL.Main.*;


public class Dmop extends Mop {
  private final Token token;
  Dmop(Token t, Scope sc) {
    super(t.toRepr(), sc);
    token = t;
  }
  public Obj call(Obj aa, Value w) {
    printdbg("dmop call", w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍺", new Variable(nsc, "⍺"));
    nsc.set("⍵", w);
    var res = Main.execLines(token, nsc);
    if (res instanceof VarArr) return ((VarArr)res).materialize();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  public Obj call(Obj aa, Value a, Value w) {
    printdbg("dmop call", a, w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍺", a);
    nsc.set("⍵", w);
    nsc.alphaDefined = true;
    var res = Main.execLines(token, nsc);
    if (res instanceof VarArr) return ((VarArr)res).materialize();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  public String toString() {
    return token.toRepr();
  }
}
