package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;
import APL.types.functions.builtins.mops.ReduceBuiltin;

public class IotaUBBuiltin extends Builtin {
  private static final Fun fn = new ReduceBuiltin().derive(new CeilingBuiltin());
  @Override public String repr() {
    return "⍸";
  }
  
  public IotaUBBuiltin(Scope sc) {
    super(sc);
  }
  public Value call(Value w) {
    int IO = sc.IO;
    int sum = (int)w.sum();
    if (w.rank == 1) {
      if (sum<0) {
        for (Value v : w) if (v.asDouble() < 0) throw new DomainError("⍸: ⍵ contained "+v, this, w);
      }
      var sub = new double[sum];
      int p = 0;
      
      if (w instanceof BitArr) {
        BitArr.BR r = ((BitArr) w).read();
        for (int i = 0; i < w.ia; i++) {
          if (r.read()) sub[p++] = i + IO;
        }
      } else {
        var da = w.asDoubleArr();
        for (int i = 0; i < w.ia; i++) {
          int v = (int) da[i];
          if (v < 0) throw new DomainError("⍸: ⍵ contained "+v, this, w);
          for (int j = 0; j < v; j++) {
            sub[p++] = i + IO;
          }
        }
      }
      return new DoubleArr(sub);
    } else {
      var sub = new Value[sum];
      int ap = 0;
      for (int[] p : new Indexer(w.shape, IO)) {
        Num n = (Num) w.at(p, IO);
        if (n.compareTo(Num.ZERO) < 0) throw new DomainError("⍸: ⍵ contained "+n, this, w);
        for (int i = 0, nint = n.asInt(); i < nint; i++) {
          sub[ap++] = Main.toAPL(p);
        }
      }
      return new HArr(sub);
    }
  }
  public Value callInv(Value w) {
    int IO = sc.IO;
    int[] sh = fn.call(w).asIntVec();
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
}