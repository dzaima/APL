package APL.types.dimensions;

import APL.*;
import APL.tokenizer.types.*;
import APL.types.*;
import APL.types.functions.builtins.fns.UpArrowBuiltin;

public class Brackets extends Obj {
  
  public final Value val;
  
  public Brackets(Value val) {
    this.val = val;
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
  
  public static Obj of(BracketTok t, Scope sc) {
    if (t.array) {
      Value[] lns = new Value[t.tokens.size()];
      for (int i = 0; i < t.tokens.size(); i++) {
        LineTok tk = t.tokens.get(i);
        lns[i] = Main.vexec(tk, sc);
      }
      return UpArrowBuiltin.merge(lns, new int[]{lns.length}, t);
    } else {
      if (t.tokens.size() == 0) return new Brackets(null);
      assert t.tokens.size() == 1; // t.array is true if size>1
      Value res = Main.vexec(t.tokens.get(0), sc);
      return new Brackets(res);
    }
  }
  
}