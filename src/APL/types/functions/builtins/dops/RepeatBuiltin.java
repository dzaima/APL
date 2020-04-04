package APL.types.functions.builtins.dops;

import APL.*;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.*;

public class RepeatBuiltin extends Dop {
  @Override public String repr() {
    return "⍣";
  }
  
  public RepeatBuiltin(Scope sc) {
    super(sc);
  }
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶');
    if (ww instanceof Fun) {
      Fun g = (Fun) ww;
      Value prev = w;
      Value curr = aaf.call(w);
      while (!Main.bool(g.call(prev, curr))) {
        Value next = aaf.call(curr);
        prev = curr;
        curr = next;
      }
      return curr;
    } else {
      int am = ((Num) ww).asInt();
      if (am < 0) {
        for (int i = 0; i < -am; i++) {
          w = aaf.callInv(w);
        }
      } else for (int i = 0; i < am; i++) {
        w = aaf.call(w);
      }
      return w;
    }
  }
  
  public Value callInv(Obj aa, Obj ww, Value w) {
    Fun aaf = isFn(aa, '⍶');
    if (ww instanceof Fun) throw new DomainError("(f⍣g)A cannot be inverted", this);
    
    int am = ((Num) ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = aaf.call(w);
      }
    } else for (int i = 0; i < am; i++) {
      w = aaf.callInv(w);
    }
    return w;
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶');
    if (ww instanceof Fun) {
      Fun g = (Fun) ww;
      Value prev = w;
      Value curr = aaf.call(a, w);
      while (!Main.bool(g.call(prev, curr))) {
        Value next = aaf.call(a, curr);
        prev = curr;
        curr = next;
      }
      return curr;
    } else {
      if (!(ww instanceof Num)) throw new SyntaxError("⍹ of ⍣ must be either a function or scalar number");
      int am = ((Num) ww).asInt();
      if (am < 0) {
        for (int i = 0; i < -am; i++) {
          w = aaf.callInvW(a, w);
        }
      } else for (int i = 0; i < am; i++) {
        w = aaf.call(a, w);
      }
      return w;
    }
  }
  
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    Fun aaf = isFn(aa, '⍶');
    if (!(ww instanceof Value)) throw new DomainError("⍢ expected ⍹ to be a number, got "+ww.humanType(true), this, ww);
    int am = ((Num) ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = aaf.call(a, w);
      }
    } else for (int i = 0; i < am; i++) {
      w = aaf.callInvW(a, w);
    }
    return w;
  }
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) {
    Fun aaf = isFn(aa, '⍶');
    if (!(ww instanceof Value)) throw new DomainError("⍢ expected ⍹ to be a number, got "+ww.humanType(true), this, ww);
    int am = ((Num) ww).asInt();
    if (am== 1) return aaf.callInvA(a, w);
    if (am==-1) return aaf.callInvA(w, a);
  
    throw new DomainError("inverting ⍺ of f⍣C is only possible when C∊¯1 1");
  }
  
  public Value under(Obj aa, Obj ww, Obj o, Value w, DerivedDop derv) {
    Fun aaf = isFn(aa, '⍶');
    if (!(ww instanceof Value)) throw new DomainError("⍢ expected ⍹ to be a number, got "+ww.humanType(true), derv, ww);
    int n = ((Value) ww).asInt();
    return repeat(aaf, n, o, w);
  }
  public Value repeat(Fun aa, int n, Obj o, Value w) { // todo don't do recursion?
    if (n==0) {
      return o instanceof Fun? ((Fun) o).call(w) : (Value) o;
    }
    
    return repeat(aa, n-1, new Fun() { public String repr() { return aa.repr(); }
      public Value call(Value w) {
        return aa.under(o, w);
      }
    }, w);
  }
}