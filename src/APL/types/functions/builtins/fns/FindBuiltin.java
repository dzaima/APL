package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.RankError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class FindBuiltin extends Builtin {
  @Override public String repr() {
    return "⍷";
  }
  
  
  
  public Obj call(Value a, Value w) {
    if (a.rank != w.rank) throw new RankError("argument ranks for ⍷ should be equal ("+a.rank+" ≠ "+w.rank+")", w);
    BitArr.BC bc = new BitArr.BC(w.shape);
    if (a.rank == 1) {
      if (a instanceof DoubleArr && w instanceof DoubleArr) {
        double[] ad = a.asDoubleArr();
        double[] wd = w.asDoubleArr();
        w: for (int ir = 0; ir < w.ia-a.ia+1; ir++) {
          for (int ia = 0; ia < a.ia; ia++) {
            if (ad[ia] != wd[ia + ir]) continue w;
          }
          bc.set(ir);
        }
      } else {
        w: for (int ir = 0; ir < w.ia-a.ia+1; ir++) {
          for (int ia = 0; ia < a.ia; ia++) {
            if (!a.get(ia).equals(w.get(ia + ir))) continue w;
          }
          bc.set(ir);
        }
      }
    } else {
      Indexer ind = new Indexer(Indexer.add(Indexer.sub(w.shape, a.shape), 1), 0);
      w: for (int[] inW : ind) {
        for (int[] inA : new Indexer(a.shape, 0)) {
          Value vA = a.simpleAt(inA);
          Value vW = w.simpleAt(Indexer.add(inA, inW));
          if (!vA.equals(vW)) continue w;
        }
        bc.set(Indexer.fromShape(w.shape, inW, 0));
      }
    }
    return bc.finish();
  }
}