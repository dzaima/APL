package APL.types.functions.builtins.mops;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.*;
import APL.types.functions.builtins.fns.UpArrowBuiltin;

public class ObliqueBuiltin extends Mop {
  @Override public String repr() {
    return "⍁";
  }
  
  

  public Value call(Obj f, Value w, DerivedMop derv) {
    isFn(f);
    if (w.rank != 2) throw new DomainError("⍁ requires a rank-2 ⍵");
    Fun ff = (Fun) f;
    int[] sz = w.shape;
    int H = sz[0];
    int W = sz[1];
    int szM = H > W? H : W;
    int szm = H > W? W : H;
    int ram = H + W - 1;
    if (ram <= 0) return new EmptyArr(EmptyArr.SHAPE0, w.safePrototype());
    
    Value[] res = new Value[ram];
    
    if (w.quickDoubleArr()) {
      double[] vals = w.asDoubleArr();
      double[][] rows = new double[ram][];
      for (int i = 0; i < ram; i++) {
        rows[i] = new double[i < szm? i + 1 : i >= szM? szm + szM - i - 1 : szm];
      }
      int p = 0;
      for (int y = 0; y < H; y++) {
        for (int x = 0; x < W; x++) {
          double v = vals[p++];
          int ri = x + y;
          int s = ri > W - 2? y + W - ri - 1 : y;
          rows[ri][s] = v;
        }
      }
      res[0] = (Value) ff.call(new DoubleArr(rows[0]));
      int rrank = res[0].rank; // required rank
      for (int i = 0; i < ram; i++) {
        Value v = (Value) ff.call(new DoubleArr(rows[i]));
        if (v.rank != rrank) throw new RankError("⍶ of ⍁ must return equal rank arrays");
        res[i] = v;
      }
    } else {
      Value[] vals = w.values();
      Value[][] rows = new Value[ram][];
      for (int i = 0; i < ram; i++) {
        rows[i] = new Value[i < szm? i + 1 : i >= szM? szm + szM - i - 1 : szm];
      }
      int p = 0;
      for (int y = 0; y < H; y++) {
        for (int x = 0; x < W; x++) {
          Value v = vals[p++];
          int ri = x + y;
          int s = ri > W - 2? y + W - ri - 1 : y;
          rows[ri][s] = v;
        }
      }
      res[0] = (Value) ff.call(new HArr(rows[0]));
      int rrank = res[0].rank; // required rank
      for (int i = 0; i < ram; i++) {
        Value v = (Value) ff.call(new HArr(rows[i]));
        if (v.rank != rrank) throw new DomainError("⍶ of ⍁ must return equal rank arrays");
        res[i] = v;
      }
    }
  
    return UpArrowBuiltin.merge(res);
  }
}