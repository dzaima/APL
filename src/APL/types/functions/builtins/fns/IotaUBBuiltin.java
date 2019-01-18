package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;
import APL.types.functions.builtins.mops.ReduceBuiltin;

public class IotaUBBuiltin extends Builtin {
  private static final Fun fn = new ReduceBuiltin().derive(new CeilingBuiltin());
  public IotaUBBuiltin(Scope sc) {
    super("⍸", sc);
  }
  
  public Obj call(Value w) {
    if (w.rank == 1) {
      int sum = (int)w.sum();
      var sub = new double[sum];
      var da = w.asDoubleArr();
      int p = 0;
      for (int i = 0; i < w.ia; i++) {
        if (da[i] < 0) throw new DomainError("⍸ received negative ⍵", w);
        for (int j = 0; j < da[i]; j++) {
          sub[p++] = (double) i + sc.IO;
        }
      }
      return new DoubleArr(sub);
    } else {
      int sum = (int)w.sum();
      var sub = new Value[sum];
      int ap = 0;
      for (int[] p : new Indexer(w.shape, sc.IO)) {
        Num n = (Num) w.at(p, sc.IO);
        if (n.compareTo(Num.ZERO) < 0) throw new DomainError("⍸ received negative ⍵", n);
        for (int i = 0, nint = n.asInt(); i < nint; i++) {
          sub[ap++] = Main.toAPL(p);
        }
      }
      return new HArr(sub);
    }
  }
  public Obj callInv(Value w) {
    int IO = sc.IO;
    int[] sh = ((Value) fn.call(w)).asIntVec();
    int ia = 1;
    for (int i = 0; i < sh.length; i++) {
      sh[i]+=1-IO;
      ia *= sh[i];
    }
    double[] arr = new double[ia];
    for (Value v : w) {
      int[] c = v.asIntVec();
      arr[Indexer.fromShape(sh, c, IO)]++;
    }
    return new DoubleArr(arr, sh);
  }
  
  public Obj call(Value a, Value w) {
    return null;
  }
}