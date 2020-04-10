package APL.types.functions.builtins.dops;

import APL.Main;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.*;
import APL.types.functions.builtins.mops.InvertBuiltin;

public class DualBuiltin extends Dop {
  @Override public String repr() {
    return "⍢";
  }
  
  
  
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    Fun wwf = isFn(ww, '⍹');
    return wwf.under(aa, w);
  }
  public Value callInv(Obj aa, Obj ww, Value w) {
    Fun aaf = isFn(aa, '⍶'); Fun wwf = isFn(ww, '⍹');
    return wwf.under(InvertBuiltin.invertM(aaf), w);
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶'); Fun wwf = isFn(ww, '⍹');
    return wwf.under(new BindA(wwf.call(a), aaf), w);
  }
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    Fun aaf = isFn(aa, '⍶'); Fun wwf = isFn(ww, '⍹');
    return wwf.under(new BindA(wwf.call(a), InvertBuiltin.invertW(aaf)), w);
  }
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) { // structural inverse is not possible; fall back to computational inverse
    Fun aaf = isFn(aa, '⍶'); Fun wwf = isFn(ww, '⍹');
    Value a1 = wwf.call(a);
    Value w1 = wwf.call(w);
    try { 
      return wwf.callInv(aaf.callInvA(a1, w1));
    } catch (DomainError e) { // but add a nice warning about it if a plausible error was received (todo better error management to not require parsing the message?)
      String msg = e.getMessage();
      if (msg.contains("doesn't support") && msg.contains("inverting")) {
        throw new DomainError(msg + " (possibly caused by using f⍢g⍨⍣¯1, which only allows computational inverses)", Main.faulty, e.cause);
      } throw e;
    }
  }
  
  public static class BindA extends Fun {
    final Value a;
    final Fun f;
    public BindA(Value a, Fun f) {
      this.a = a;
      this.f = f;
    }
  
    public Value call(Value w) {
      return f.call(a, w);
    }
    public Value callInv(Value w) {
      return f.callInvW(a, w);
    }
  
    public String repr() {
      return f.repr();
    }
  }
}