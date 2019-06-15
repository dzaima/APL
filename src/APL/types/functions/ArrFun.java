package APL.types.functions;

import APL.*;
import APL.tokenizer.types.BacktickTok;
import APL.types.*;
import APL.types.arrs.HArr;

public class ArrFun extends Primitive {
  
  private final Fun f;
  
  public ArrFun(Fun f) {
    this.f = f;
  }
  
  public ArrFun(BacktickTok t, Scope sc) {
    f = (Fun) Main.oexec(t.value(), sc);
  }
  
  public Fun fun() {
    return f;
  }
  
  @Override public Value ofShape(int[] sh) {
    if (sh.length == 0) return this;
    return new HArr(new Value[]{this}, sh);
  }
  
  @Override public String toString() {
    return "`"+f.repr();
  }
}
