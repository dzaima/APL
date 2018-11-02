package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class GradeDownBuiltin extends Builtin {
  public GradeDownBuiltin(Scope sc) {
    super("⍒", 0x001, sc);
  }
  
  public Obj call(Value w) {
    Integer[] na = w.gradeDown();
    return new DoubleArr(na);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}