package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

public class RTackBuiltin extends Builtin {
  @Override public String repr() {
    return "⊢";
  }
  
  
  
  public Value call(Value w) { return w; }
  public Value call(Value a, Value w) { return w; }
  
  public Value callInv(Value w) {
    return w;
  }
  public Value callInvW(Value a, Value w) {
    return w;
  }
  public Value callInvA(Value a, Value w) {
    throw new DomainError("⊣⍣¯1 is impossible");
  }
}