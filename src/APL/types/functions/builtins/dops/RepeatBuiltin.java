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
    isFn(aa, '⍶');
    Fun f = (Fun) aa;
    if (ww instanceof Fun) {
      Fun g = (Fun) ww;
      Value prev = w;
      Value curr = f.call(w);
      while (!Main.bool(g.call(prev, curr))) {
        Value next = f.call(curr);
        prev = curr;
        curr = next;
      }
      return curr;
    } else {
      int am = ((Num) ww).asInt();
      if (am < 0) {
        for (int i = 0; i < -am; i++) {
          w = f.callInv(w);
        }
      } else for (int i = 0; i < am; i++) {
        w = f.call(w);
      }
      return w;
    }
  }
  
  public Value callInv(Obj aa, Obj ww, Value w) {
    isFn(aa, '⍶');
    Fun f = (Fun) aa;
    if (ww instanceof Fun) throw new DomainError("(f⍣g)A cannot be inverted", this);
    
    int am = ((Num) ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = f.call(w);
      }
    } else for (int i = 0; i < am; i++) {
      w = f.callInv(w);
    }
    return w;
  }
  
  public Value call(Obj aa, Obj ww, Value a, Value w, DerivedDop derv) {
    isFn(aa, '⍶');
    Fun f = (Fun) aa;
    if (ww instanceof Fun) {
      Fun g = (Fun) ww;
      Value prev = w;
      Value curr = f.call(a, w);
      while (!Main.bool(g.call(prev, curr))) {
        Value next = f.call(a, curr);
        prev = curr;
        curr = next;
      }
      return curr;
    } else {
      if (!(ww instanceof Num)) throw new SyntaxError("⍹ of ⍣ must be either a function or scalar number");
      int am = ((Num) ww).asInt();
      if (am < 0) {
        for (int i = 0; i < -am; i++) {
          w = f.callInvW(a, w);
        }
      } else for (int i = 0; i < am; i++) {
        w = f.call(a, w);
      }
      return w;
    }
  }
  
  public Value callInvW(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶');
    int am = ((Num)ww).asInt();
    if (am < 0) {
      for (int i = 0; i < -am; i++) {
        w = ((Fun)aa).call(a, w);
      }
    } else for (int i = 0; i < am; i++) {
      w = ((Fun)aa).callInvW(a, w);
    }
    return w;
  }
  public Value callInvA(Obj aa, Obj ww, Value a, Value w) {
    isFn(aa, '⍶');
    int am = ((Num)ww).asInt();
    if (am== 1) return ((Fun) aa).callInvA(a, w);
    if (am==-1) return ((Fun) aa).callInvA(w, a);
  
    throw new DomainError("inverting ⍺ of f⍣C is only possible when C∊¯1 1");
  }
  
  public boolean strInv(Obj aa, Obj ww) {
    return aa instanceof Fun && ((Fun) aa).strInv() && ww instanceof Num;
  }
  public Value strInv(Obj aa, Obj ww, Value w, Value origW) {
    int n = ((Num) ww).asInt();
    Value[] origs = new Value[n];
    Value corig = origW;
    for (int i = 0; i < n; i++) {
      origs[i] = corig;
      corig = ((Fun) aa).call(corig);
    }
    Value c = w;
    for (int i = n-1; i >= 0; i--) {
      c = ((Fun) aa).strInv(c, origs[i]);
    }
    return c;
  }
}