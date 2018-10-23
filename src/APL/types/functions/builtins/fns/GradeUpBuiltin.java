package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;

public class GradeUpBuiltin extends Builtin {
  public GradeUpBuiltin(Scope sc) {
    super("⍋", 0x001, sc);
  }
  
  public Obj call(Value w) {
    Integer[] na = w.gradeUp(this);
    Num[] res = new Num[w.ia];
    int IO = ((Num) sc.get("⎕IO")).intValue();
    for (int i = 0; i < na.length; i++) {
      res[i] = new Num(na[i]+IO);
    }
    return new Arr(res);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}