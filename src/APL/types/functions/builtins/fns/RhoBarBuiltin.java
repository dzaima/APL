package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.DomainError;
import APL.types.Value;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class RhoBarBuiltin extends Builtin {
  @Override public String repr() {
    return "ϼ";
  }
  
  public RhoBarBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    int IO = sc.IO;
    if (w.rank != 1) throw new DomainError("argument to ϼ must be a vector");
    int dim = w.ia;
    int[] shape = w.asIntVec();
    int prod = 1;
    for (int n : shape) prod*= n;
    Value[] res = new Value[dim];
    int blockSize = prod;
    for (int i = 0; i < dim; i++) {
      blockSize/= shape[i];
      double[] ds = new double[prod];
      int len = shape[i];
      int j = 0;
      while (j < prod) {
        for (int k = 0; k < len; k++) {
          int val = k+IO;
          for (int l = 0; l < blockSize; l++) {
            ds[j++] = val;
          }
        }
      }
      res[i] = new DoubleArr(ds, shape);
    }
    return new HArr(res);
  }
  
}