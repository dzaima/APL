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
    w: for (int[] inW : new Indexer(Indexer.add(Indexer.sub(w.shape, a.shape), 1), 0)) {
      for (int[] inA : new Indexer(a.shape, 0)) {
        Value vA = a.simpleAt(inA);
        Value vW = w.simpleAt(Indexer.add(inA, inW));
        if (!vA.equals(vW)) continue w;
      }
      bc.set(Indexer.fromShape(w.shape, inW, 0));
    }
    return bc.finish();
  }
}