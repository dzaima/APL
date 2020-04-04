package APL.types.functions.builtins.mops;

import APL.errors.NYIError;
import APL.types.*;
import APL.types.functions.*;

public class InvertBuiltin extends Mop { // separate ⍵-inverse (equal to f⍣¯1)
  
  public Value call(Obj f, Value w, DerivedMop derv) {
    Fun ff = isFn(f);
    return ff.callInv(w);
  }
  public Value call(Obj f, Value a, Value w, DerivedMop derv) {
    Fun ff = isFn(f);
    return ff.callInvW(a, w);
  }
  public Value callInvW(Obj f, Value a, Value w) {
    Fun ff = isFn(f);
    return ff.call(a, w);
  }
  public Value callInvA(Obj f, Value a, Value w) {
    throw new NYIError("InvertBuiltin invert ⍺", this);
  }
  
  public static Fun invertM(Fun f) {
    return new Fun() {
      public String repr() { return f.repr()+"⍣¯1"; }
      public Value call(Value w) {
        return f.callInv(w);
      }
    };
  }
  public static Fun invertW(Fun f) {
    return new Fun() {
      public String repr() { return f.repr()+"⍣¯1"; }
      public Value call(Value a, Value w) {
        return f.callInvW(a, w);
      }
  
      public Value callInvW(Value a, Value w) {
        return f.call(a, w);
      }
    };
  }
  public static Fun invertA(Fun f) {
    return new Fun() {
      public String repr() { return f.repr()+"⍨⍣¯1⍨"; }
      public Value call(Value a, Value w) {
        return f.callInvA(a, w);
      }
  
      public Value callInvA(Value a, Value w) {
        return f.call(a, w);
      }
    };
  }
  
  public String repr() {
    return "¯";
  }
}
