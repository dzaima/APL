package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;
import APL.types.functions.VarArr;

public class Dfn extends Fun {
  private final Token token;
  private final Scope psc;
  Dfn(Token t, Scope sc) {
    token = t;
    psc = sc;
    valid = 0x011;
  }
  public Obj call(Value w) {
    Main.printdbg("dfn call", w);
    sc = new Scope(psc);
    sc.set("⍺", new Variable(sc, "⍺"));
    sc.set("⍵", w);
    var res = Main.execLines(token, sc);
    if (res instanceof VarArr) return ((VarArr)res).materialize();
    return res;
  }
  public Obj call(Value a, Value w) {
    Main.printdbg("dfn call", a, w);
    sc = new Scope(psc);
    sc.set("⍺", a);
    sc.set("⍵", w);
    sc.alphaDefined = true;
    var res = Main.execLines(token, sc);
    if (res instanceof VarArr) return ((VarArr)res).materialize();
    return res;
  }
  public String toString() {
    return token.toRepr();
  }
  
  @Override
  public Type type() {
    return Type.fn;
  }
  
  public String name() { return "dfn"; }
}