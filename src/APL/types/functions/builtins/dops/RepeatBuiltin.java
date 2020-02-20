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
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    isFn(aa, '⍶');
    Fun f = (Fun) aa;
    if (ww instanceof Fun) {
      Fun g = (Fun) ww;
      Value prev = w;
      Value curr = (Value) f.call(w);
      while (!Main.bool(g.call(prev, curr))) {
        Value next = (Value) f.call(curr);
        prev = curr;
        curr = next;
      }
      return curr;
    } else {
      int am = ((Num) ww).asInt();
      if (am < 0) {
        for (int i = 0; i < -am; i++) {
          w = (Value) f.callInv(w);
        }
      } else for (int i = 0; i < am; i++) {
        w = (Value) f.call(w);
      }
      return w;
    }
  }
  
  public Obj callInv(Obj aa, Obj ww, Value w) {
    isFn(aa, '⍶');
    Fun f = (Fun) aa;
    if (ww instanceof Fun) throw new DomainError("(f⍣g)A cannot be inverted", this);
    
    int am = ((Num) ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = (Value) f.call(w);
      }
    } else for (int i = 0; i < am; i++) {
      w = (Value) f.callInv(w);
    }
    return w;
  }
  
  public Obj call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶');
    Fun f = (Fun) aa;
    if (ww instanceof Fun) {
      Fun g = (Fun) ww;
      Value prev = w;
      Value curr = (Value) f.call(a, w);
      while (!Main.bool(g.call(prev, curr))) {
        Value next = (Value) f.call(a, curr);
        prev = curr;
        curr = next;
      }
      return curr;
    } else {
      if (!(ww instanceof Num)) throw new SyntaxError("⍹ of ⍣ must be either a function or scalar number");
      int am = ((Num) ww).asInt();
      if (am < 0) {
        for (int i = 0; i < -am; i++) {
          w = (Value) f.callInvW(a, w);
        }
      } else for (int i = 0; i < am; i++) {
        w = (Value) f.call(a, w);
      }
      return w;
    }
  }
  
  public Obj callInvW(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶');
    int am = ((Num)ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = (Value)((Fun)aa).call(a, w);
      }
    } else for (int i = 0; i < am; i++) {
      w = (Value)((Fun)aa).callInvW(a, w);
    }
    return w;
  }
  public Obj callInvA(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶');
    int am = ((Num)ww).asInt();
    if (am== 1) return ((Fun) aa).callInvA(a, w);
    if (am==-1) return ((Fun) aa).callInvA(w, a);
  
    throw new DomainError("inverting ⍺ of f⍣C is only possible when C∊¯1 1");
  }
}