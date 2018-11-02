package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;
import APL.types.functions.VarArr;

public class Dfn extends Fun {
  private final Token token;
  Dfn(Token t, Scope sc) {
    super( 0x011, sc);
    token = t;
  }
  public Obj call(Value w) {
    Main.printdbg("dfn call", w);
    Scope nsc = new Scope(sc);
    nsc.set("⍺", new Variable(nsc, "⍺"));
    nsc.set("⍵", w);
    var res = Main.execLines(token, nsc);
    if (res instanceof VarArr) return ((VarArr)res).materialize();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  public Obj call(Value a, Value w) {
    Main.printdbg("dfn call", a, w);
    Scope nsc = new Scope(sc);
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
  
  @Override
  public Type type() {
    return Type.fn;
  }
  
  public String name() { return "dfn"; }
}