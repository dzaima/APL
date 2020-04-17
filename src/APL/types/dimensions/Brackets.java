package APL.types.dimensions;

import APL.*;
import APL.errors.SyntaxError;
import APL.tokenizer.types.BracketTok;
import APL.types.*;
import APL.types.functions.VarArr;

public class Brackets extends Obj {
  
  public Value val;
  
  public Brackets(BracketTok t, Scope sc) {
    if (t.tokens.size() != 0) {
      if (t.tokens.size() != 1) throw new SyntaxError("multiple statements in bracket indexing");
      Obj res = Main.exec(t.tokens.get(0), sc);
      if (res instanceof VarArr) res = ((VarArr) res).get();
      if (res instanceof Variable) res = ((Variable) res).get();
      if (!(res instanceof Value)) throw new SyntaxError("brackets contained " + res.humanType(true));
      val = (Value) res;
    }
  }
  
  public Integer toInt() {
    return val==null? null : val.asInt();
  }
  public int[] toInts() {
    return val==null? null : val.asIntVec();
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