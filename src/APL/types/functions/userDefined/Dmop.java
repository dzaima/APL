package APL.types.functions.userDefined;

import APL.*;
import APL.tokenizer.types.DfnTok;
import APL.types.*;
import APL.types.functions.*;

import static APL.Main.*;


public class Dmop extends Mop {
  public final DfnTok code;
  
  @Override public String repr() {
    return code.toRepr();
  }
  
  Dmop(DfnTok t, Scope sc) {
    super(sc);
    code = t;
  }
  public Obj call(Obj aa, Value w, DerivedMop derv) {
    printdbg("dmop call", w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍺", new Variable(nsc, "⍺"));
    nsc.set("⍵", w);
    nsc.set("∇", derv);
    var res = Main.execLines(code, nsc);
    if (res instanceof VarArr) return ((VarArr)res).materialize();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  public Obj call(Obj aa, Value a, Value w, DerivedMop derv) {
    printdbg("dmop call", a, w);
    Scope nsc = new Scope(sc);
    nsc.set("⍶", aa);
    nsc.set("⍺", a);
    nsc.set("⍵", w);
    nsc.set("∇", derv);
    nsc.alphaDefined = true;
    var res = Main.execLines(code, nsc);
    if (res instanceof VarArr) return ((VarArr)res).materialize();
    if (res instanceof Settable) return ((Settable)res).get();
    return res;
  }
  public String toString() {
    return code.toRepr();
  }
}
