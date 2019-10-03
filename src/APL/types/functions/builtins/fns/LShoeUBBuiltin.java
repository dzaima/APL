package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class LShoeUBBuiltin extends Builtin {
  @Override public String repr() {
    return "⊇";
  }
  
  public LShoeUBBuiltin(Scope sc) {
    super(sc);
  }
  
  public Obj call(Value w) {
    if (w.rank == 0) return w.get(0);
    return w;
  }
  
  public Obj call(Value a, Value w) {
//    return new EachBuiltin().derive(new SquadBuiltin(sc)).call(a, (Value)new LShoeBuiltin().call(w));
    if (w.ia == 0) return EmptyArr.SHAPE0;
    if (a instanceof APLMap) {
      Value[] res = new Value[w.ia];
      APLMap map = (APLMap) a;
      Value[] vs = w.values();
      for (int i = 0; i < w.ia; i++) {
        res[i] = (Value) map.getRaw(vs[i].asString());
      }
      return Arr.create(res, w.shape);
    }
    if (w instanceof Primitive) return a.get((int) w.asDouble() - sc.IO);
  
    if (Main.vind) { // ⎕VI←1
  
  
      double[][] ind = Indexer.inds(w);
      int ml = ind[0].length;
      if (a.quickDoubleArr()) {
        double[] wv = a.asDoubleArr();
        double[] res = new double[ml];
        for (int i = 0; i < ml; i++) {
          res[i] = wv[Indexer.ind(a.shape, ind, i, sc.IO)];
        }
        return new DoubleArr(res, w.shape);
      }
      Value[] res = new Value[ml];
      for (int i = 0; i < ml; i++) {
        res[i] = a.ind(ind, i, sc.IO);
      }
      return Arr.create(res, Indexer.indsh(w));
      
    } else { // ⎕VI←0
  
      if (a.quickDoubleArr()) {
        double[] wv = a.asDoubleArr();
        double[] res = new double[w.ia];
        if (w.quickDoubleArr()) {
          double[] da = w.asDoubleArr();
          for (int i = 0; i < w.ia; i++) {
            res[i] = wv[Indexer.fromShape(a.shape, new int[]{(int) da[i]}, sc.IO)];
          }
        } else {
          for (int i = 0; i < w.ia; i++) {
            res[i] = wv[Indexer.fromShape(a.shape, w.get(i).asIntVec(), sc.IO)];
          }
        }
        return new DoubleArr(res, w.shape);
      }
      Value[] res = new Value[w.ia];
      for (int i = 0; i < w.ia; i++) {
        res[i] = a.at(w.get(i).asIntVec(), sc.IO);
      }
      return Arr.create(res, w.shape);
    }
  }
}