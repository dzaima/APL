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
        double[] da = w.asDoubleArr();
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
      double[] wd = w.asDoubleArr();
      if (Main.vind) { // ⎕VI←1
        double[][] res = new double[w.rank][sum];
        int ri = 0;
        Indexer idx = new Indexer(w.shape, IO);
        int rank = res.length;
        for (int i = 0; i < w.ia; i++) {
          int[] p = idx.next();
          int n = Num.toInt(wd[idx.pos()]);
          if (n > 0) {
            for (int k = 0; k < rank; k++) {
              for (int j = 0; j < n; j++) res[k][ri+j] = p[k];
            }
            ri+= n;
          } else if (n != 0) throw new DomainError("⍸: ⍵ contained "+n, this, w);
        }
        Value[] resv = new Value[rank];
        for (int i = 0; i < rank; i++) resv[i] = new DoubleArr(res[i]);
        return new HArr(resv);
      } else { // ⎕VI←0
        Value[] res = new Value[sum];
        int ri = 0;
        Indexer idx = new Indexer(w.shape, IO);
        for (int i = 0; i < w.ia; i++) {
          int[] p = idx.next();
          int n = Num.toInt(wd[idx.pos()]);
          if (n > 0) {
            DoubleArr pos = Main.toAPL(p);
            for (int j = 0; j < n; j++) res[ri++] = pos;
          } else if (n != 0) throw new DomainError("⍸: ⍵ contained "+n, this, w);
        }
        return new HArr(res);
      }
    }
  }
  public Value callInv(Value w) {
    int IO = sc.IO;
    int[] sh = fn.call(w).asIntVec();
    int ia = 1;
    for (int i = 0; i < sh.length; i++) {
      sh[i] = Math.max(0, sh[i] + 1-IO);
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