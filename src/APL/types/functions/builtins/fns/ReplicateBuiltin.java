package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class ReplicateBuiltin extends Builtin {
  @Override public String repr() {
    return "⌿";
  }
  
  
  
  public Obj call(Value a, Value w) {
    if (a.rank == 0) {
      RankError.must(w.rank < 2, "rank of ⍵ for ⌿ should be ≤1 if ⍺ is a scalar");
      int sz = a.asInt();
      if (sz < 0) {
        Value[] res = new Value[w.ia*-sz];
        Value n = w.first() instanceof Char? Char.SPACE : Num.ZERO;
        Arrays.fill(res, n);
        return Arr.create(res);
      }
      Value[] res = new Value[w.ia*sz];
      int ptr = 0;
      for (int i = 0; i < w.ia; i++) {
        Value c = w.get(i);
        for (int j = 0; j < sz; j++) {
          res[ptr++] = c;
        }
      }
      return Arr.create(res);
    }
    
    // ⍺.rank ≠ 0
    RankError.must(a.rank == w.rank, "shapes of ⍺ & ⍵ of ⌿ must be equal (rank "+a.rank+" vs "+w.rank + ")");
    LengthError.must(Arrays.equals(a.shape, w.shape), "shapes of ⍺ & ⍵ of ⌿ must be equal ("+ Main.formatAPL(a.shape) + " vs " + Main.formatAPL(w.shape) + ")");
  
    if (a instanceof BitArr) {
      BitArr ab = (BitArr) a;
      ab.setEnd(false);
      int sum = ab.isum();
      if (w.quickDoubleArr()) {
        if (sum > w.ia*.96) {
          double[] ds = w.asDoubleArr();
          double[] res = new double[sum];
          
          long[] la = ab.arr;
          int l = la.length;
          int am = 0, pos = 0;
          for (int i = 0; i < l; i++) {
            long c = la[i];
            for (int s = 0; s < 64; s++) {
              if ((c&1) == 0) {
                if (am != 0) System.arraycopy(ds, i*64 + s - am, res, pos, am);
                pos+= am;
                am = 0;
              } else am++;
              c>>= 1;
            }
          }
          if (am > 0) System.arraycopy(ds, ds.length - am, res, pos, am);
          return new DoubleArr(res);
        }
        double[] ds = w.asDoubleArr();
        double[] res = new double[sum];
        long[] la = ab.arr;
        int l = la.length;
        int pos = 0;
        for (int i = 0; i < l; i++) {
          long c = la[i];
          for (int s = 0; s < 64; s++) {
            if ((c&1) != 0) {
              res[pos++] = ds[i*64 + s];
            }
            c>>= 1;
          }
        }
        return new DoubleArr(res);
        // BitArr.BR r = ab.read();
        // int pos = 0;
        // for (int i = 0; i < w.ia; i++) {
        //   if (r.read()) {
        //     res[pos++] = ds[i];
        //   }
        // }
        // return new DoubleArr(res);
      }
      Value[] res = new Value[sum];
      BitArr.BR r = ab.read();
      int pos = 0;
      for (int i = 0; i < w.ia; i++) {
        if (r.read()) {
          res[pos++] = w.get(i);
        }
      }
      return Arr.create(res);
    }
    
    
    int total = 0;
    int[] sizes = a.asIntArr();
    for (int i = 0; i < a.ia; i++) {
      total+= Math.abs(sizes[i]);
    }
    
    if (w.quickDoubleArr()) {
      int ptr = 0;
      double[] wi = w.asDoubleArr();
      double[] res = new double[total];
      for (int i = 0; i < a.ia; i++) {
        double c = wi[i];
        int am = sizes[i];
        if (sizes[i] < 0) {
          for (int j = 0; j > am; j--) {
            res[ptr++] = 0;
          }
        } else {
          for (int j = 0; j < am; j++) {
            res[ptr++] = c;
          }
        }
      }
      return new DoubleArr(res);
      
    } else {
      int ptr = 0;
      Value[] res = new Value[total];
      for (int i = 0; i < a.ia; i++) {
        Value c = w.get(i);
        int am = sizes[i];
        if (sizes[i] < 0) {
          am = -am;
          c = c.prototype();
        }
        for (int j = 0; j < am; j++) {
          res[ptr++] = c;
        }
      }
      return Arr.create(res);
    }
  }
}