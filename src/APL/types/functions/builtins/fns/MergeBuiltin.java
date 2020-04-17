package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class MergeBuiltin extends Builtin {
  
  public MergeBuiltin(Scope sc) {
    super(sc);
  }
  
  @Override public Value call(Value a, Value w) {
    if (w.rank != 1) throw new DomainError("%: ⍵ must be a vector", this, w);
    int IO = sc.IO;
    int[] sh = a.shape;
    int i1 = 0;
    boolean allds = true;
    for (Value v : w) {
      if (!Arrays.equals(v.shape, sh)) throw new DomainError("%: shape of item "+(i1+IO)+" in ⍵ didn't match ⍺ ("+Main.formatAPL(sh)+" vs "+Main.formatAPL(v.shape)+")", this, w);
      i1++;
      if (!v.quickDoubleArr()) allds = false;
    }
    // if (IO==0 && a instanceof BitArr) { TODO
    //   
    // }
    if (allds) {
      double[] ds = new double[a.ia];
      double[][] wds = new double[w.ia][];
      for (int i = 0; i < w.ia; i++) wds[i] = w.get(i).asDoubleArr();
      double[] idx = a.asDoubleArr();
      for (int i = 0; i < idx.length; i++) {
        ds[i] = wds[(int)idx[i] - IO][i];
      }
      if (a.rank == 0) return new Num(ds[0]);
      return new DoubleArr(ds, a.shape);
    }
    Value[] vs = new Value[a.ia];
    double[] idx = a.asDoubleArr();
    for (int i = 0; i < idx.length; i++) {
      vs[i] = w.get((int)idx[i] - IO).get(i);
    }
    return Arr.createL(vs, a.shape);
  }
  
  @Override public String repr() {
    return "%";
  }
}