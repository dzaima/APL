package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.functions.Builtin;

public class GradeDownBuiltin extends Builtin {
  public GradeDownBuiltin(Scope sc) {
    super("⍒");
    valid = 0x001;
    this.sc = sc;
  }
  
  public Obj call(Value w) {
    Integer[] na = w.gradeDown(this);
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