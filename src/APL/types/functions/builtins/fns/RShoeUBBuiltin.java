package APL.types.functions.builtins.fns;

import APL.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class RShoeUBBuiltin extends Builtin {
  public RShoeUBBuiltin(Scope sc) {
    super("⊇", 0x011, sc);
  }
  
  public Obj call(Value w) {
    if (w.rank == 0) return w.get(0);
    return w;
  }
  
  public Obj call(Value a, Value w) {
//    return new EachBuiltin().derive(new SquadBuiltin(sc)).call(a, (Value)new LShoeBuiltin().call(w));
    if (a.ia == 0) return EmptyArr.SHAPE0;
    if (w.quickDoubleArr()) {
      double[] wv = w.asDoubleArr();
      double[] res = new double[a.ia];
      for (int i = 0; i < a.ia; i++) {
        res[i] = wv[Indexer.fromShape(w.shape, a.get(i).asIntArr(), sc.IO)];
      }
      return new DoubleArr(res, a.shape);
    }
    Value[] res = new Value[a.ia];
    for (int i = 0; i < a.ia; i++) {
      res[i] = w.at(a.get(i).asIntArr(), sc.IO);
    }
    return new HArr(res, a.shape);
  }
}