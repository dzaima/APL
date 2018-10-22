package APL.types.dimensions;

import APL.*;
import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.VarArr;

public class Brackets extends Obj {
  
  public Value val;
  
  public Brackets(Token t, Scope sc) {
    if (t.tokens.size() != 0) {
      Obj res = Main.execTok(t, sc);
      if (res instanceof VarArr) res = ((VarArr) res).materialize();
      if (res instanceof Variable) res = ((Variable) res).get();
      if (!(res instanceof Value)) throw new SyntaxError("brackets contained " + res.humanType(true));
      val = (Value) res;
    }
  }
  
  public Integer toInt() {
    return val == null ? null : val.toInt(null);
  }
  
  @Override
  public Type type() {
    return Type.dim;
  }
  
  @Override
  public String toString() {
    return "["+val+"]";
  }
}