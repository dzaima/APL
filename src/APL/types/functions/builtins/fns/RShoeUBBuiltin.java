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
  
  public Value call(Value a, Value w) {
    return on(a, w, sc.IO, this);
  }
  
  public static Value on(Value a, Value w, int IO, Callable blame) {
    if (w instanceof APLMap) {
      Value[] res = new Value[a.ia];
      APLMap map = (APLMap) w;
      Value[] ks = a.values();
      for (int i = 0; i < a.ia; i++) res[i] = (Value) map.getRaw(ks[i]);
      return Arr.createL(res, a.shape);
    }
    if (a instanceof Primitive && w.rank==1) {
      Value r = w.get((int) a.asDouble() - IO);
      if (r instanceof Primitive) return r;
      else return new Rank0Arr(r);
    }
    
    return on(Indexer.poss(a, w.shape, IO, blame), w);
  }
  
  public static Value on(Indexer.PosSh poss, Value w) {
    if (w.quickDoubleArr()) {
      double[] res = new double[Arr.prod(poss.sh)];
      double[] wd = w.asDoubleArr();;
      int[] idxs = poss.vals;
      for (int i = 0; i < idxs.length; i++) {
        res[i] = wd[idxs[i]];
      }
      return DoubleArr.safe(res, poss.sh);
    }
    Value[] res = new Value[Arr.prod(poss.sh)];
    int[] idxs = poss.vals;
    for (int i = 0; i < idxs.length; i++) {
      res[i] = w.get(idxs[i]);
    }
    return Arr.createL(res, poss.sh);
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