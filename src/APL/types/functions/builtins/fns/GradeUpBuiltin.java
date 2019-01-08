package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class GradeUpBuiltin extends Builtin {
  public GradeUpBuiltin(Scope sc) {
    super("⍋", 0x001, sc);
  }
  
  public Obj call(Value w) {
    double[] res = new double[w.ia];
    int IO = sc.IO;
    Integer[] na = w.gradeUp();
    for (int i = 0; i < na.length; i++) {
      res[i] = na[i]+IO;
    }
    return new DoubleArr(res);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}