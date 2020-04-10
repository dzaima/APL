package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class RShoeUBBuiltin extends Builtin {
  @Override public String repr() {
    return "⊇";
  }
  
  public RShoeUBBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    if (w.rank == 0) return w.get(0);
    return w;
  }
  
  public Value call(Value a, Value w) {
    return on(a, w, sc.IO);
  }
  
  public static Value on(Value a, Value w, int IO) {
    if (a.ia == 0) return EmptyArr.SHAPE0Q;
    if (w instanceof APLMap) {
      Value[] res = new Value[a.ia];
      APLMap map = (APLMap) w;
      Value[] vs = a.values();
      for (int i = 0; i < a.ia; i++) {
        res[i] = (Value) map.getRaw(vs[i].asString());
      }
      return Arr.create(res, a.shape);
    }
    if (a instanceof Primitive) {
      Value r = w.get((int) a.asDouble() - IO);
      if (r instanceof Primitive) return r;
      else return new Rank0Arr(r);
    }
  
    if (Main.vind) { // ⎕VI←1
  
  
      double[][] ind = Indexer.inds(a);
      int ml = ind[0].length;
      if (w.quickDoubleArr()) {
        double[] wv = w.asDoubleArr();
        double[] res = new double[ml];
        for (int i = 0; i < ml; i++) {
          res[i] = wv[Indexer.ind(w.shape, ind, i, IO)];
        }
        return new DoubleArr(res, a.shape);
      }
      Value[] res = new Value[ml];
      for (int i = 0; i < ml; i++) {
        res[i] = w.ind(ind, i, IO);
      }
      return Arr.create(res, Indexer.indsh(a));
      
    } else { // ⎕VI←0
  
      if (w.quickDoubleArr()) {
        double[] wv = w.asDoubleArr();
        double[] res = new double[a.ia];
        if (a.quickDoubleArr()) {
          double[] da = a.asDoubleArr();
          for (int i = 0; i < a.ia; i++) {
            res[i] = wv[Indexer.fromShape(w.shape, new int[]{(int) da[i]}, IO)];
          }
        } else {
          for (int i = 0; i < a.ia; i++) {
            res[i] = wv[Indexer.fromShape(w.shape, a.get(i).asIntVec(), IO)];
          }
        }
        return new DoubleArr(res, a.shape);
      }
      Value[] res = new Value[a.ia];
      for (int i = 0; i < a.ia; i++) {
        res[i] = w.at(a.get(i).asIntVec(), IO);
      }
      return Arr.create(res, a.shape);
    }
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(a, w)) : (Value) o;
    Value[] vs = w.valuesCopy();
    for (int i = 0; i < a.ia; i++) {
      vs[Indexer.fromShape(w.shape, a.get(i).asIntVec(), sc.IO)] = v.get(i);
    }
    return Arr.createL(vs, w.shape);
  }
}