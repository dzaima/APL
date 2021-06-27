package APL.types.functions;

import APL.*;
import APL.errors.DomainError;
import APL.tokenizer.types.BacktickTok;
import APL.types.*;
import APL.types.arrs.SingleItemArr;

public class ArrFun extends Primitive {
  
  private final Obj f;
  
  public ArrFun(Fun f) {
    this.f = f;
  }
  
  public ArrFun(BacktickTok t, Scope sc) {
    f = Main.exec(t.value(), sc);
    if (!(f instanceof Fun) && !(f instanceof Mop) && !(f instanceof Dop)) {
      throw new DomainError("can't arrayify " + f.humanType(true), this);
    }
  }
  
  public Obj obj() {
    return f;
  }
  
  @Override public Value ofShape(int[] sh) {
    return SingleItemArr.maybe(this, sh);
  }
  
  @Override public String toString() {
    if (f instanceof Fun) return "`"+((Fun) f).repr();
    if (f instanceof Mop) return "`"+((Mop) f).repr();
    if (f instanceof Dop) return "`"+((Dop) f).repr();
    throw new InternalError("unexpected `"+f.humanType(false));
  }
  
  @Override public int hashCode() {
    return f.hashCode();
  }
  
  @Override public boolean equals(Obj o) {
    if (!(o instanceof ArrFun)) return false;
    ArrFun w = (ArrFun) o;
    return w.f.equals(f);
  }
}