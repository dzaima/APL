package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.types.Value;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

public class GradeDownBuiltin extends Builtin {
  @Override public String repr() {
    return "⍒";
  }
  
  public GradeDownBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    double[] res = new double[w.ia];
    int IO = sc.IO;
    Integer[] na = w.gradeDown();
    for (int i = 0; i < na.length; i++) {
      res[i] = na[i]+IO;
    }
    return new DoubleArr(res);
  }
  
//  public Obj call(Value a, Value w) {
//
//  }
}