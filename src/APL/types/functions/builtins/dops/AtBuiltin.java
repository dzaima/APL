package APL.types.functions.builtins.dops;

import APL.*;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.*;
import APL.types.functions.builtins.fns.RShoeUBBuiltin;

public class AtBuiltin extends Dop {
  @Override public String repr() {
    return "@";
  }
  
  public AtBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    return at(aa, ww, w, sc.IO, this);
  }
  
  public static Value at(Obj aa, Obj ww, Value w, int IO, Callable blame) {
    int ia = w.ia;
    if (ww instanceof Fun) {
      Value vba = ((Fun) ww).call(w);
      boolean[] ba = new boolean[ia];
      int matchingCount = 0;
      for (int i = 0; i < ia; i++) {
        ba[i] = Main.bool(vba.get(i));
        if (ba[i]) matchingCount++;
      }
      Value aaa;
      if (aa instanceof Fun) {
        Value[] matching = new Value[matchingCount];
        int ptr = 0;
        for (int i = 0; i < ia; i++) {
          if (ba[i]) matching[ptr++] = w.get(i);
        }
        aaa = ((Fun) aa).call(Arr.create(matching));
      } else aaa = (Value) aa;
      Value[] ra = new Value[ia];
      if (aaa.rank == 0) {
        Value inner = aaa.get(0);
        for (int i = 0; i < ia; i++) {
          if (ba[i]) ra[i] = inner;
          else ra[i] = w.get(i);
        }
      } else {
        int ptr = 0;
        for (int i = 0; i < ia; i++) {
          if (ba[i]) ra[i] = aaa.get(ptr++);
          else ra[i] = w.get(i);
        }
      }
      return Arr.createL(ra, w.shape);
    } else {
      Value wwa = (Value) ww;
  
      Indexer.PosSh poss = Indexer.poss(wwa, w.shape, IO, blame);
      Value repl;
      if (aa instanceof Fun) {
        Fun aaf = ((Fun) aa);
        Value arg = RShoeUBBuiltin.on(poss, w);
        repl = aaf.call(arg);
      } else {
        repl = (Value) aa;
      }
      return with(w, poss, repl, blame);
    }
  }
  
  public static Value with(Value o, Indexer.PosSh poss, Value n, Callable blame) {
    if (o.quickDoubleArr() && n.quickDoubleArr()) {
      double[] res = o.asDoubleArrClone();
      int[] is = poss.vals;
      if (n.rank == 0) {
        double aafst = n.first().asDouble();
        // noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < is.length; i++) res[is[i]] = aafst;
      } else {
        double[] nd = n.asDoubleArr();
        Arr.eqShapes(n.shape, poss.sh, blame);
        for (int i = 0; i < is.length; i++) res[is[i]] = nd[i];
      }
      return o.rank==0? Num.of(res[0]) : new DoubleArr(res, o.shape);
    }
    Value[] res = o.valuesCopy();
    int[] is = poss.vals;
    if (n.rank == 0) {
      Value aafst = n.first();
      // noinspection ForLoopReplaceableByForEach
      for (int i = 0; i < is.length; i++) res[is[i]] = aafst;
    } else {
      Arr.eqShapes(n.shape, poss.sh, blame);
      for (int i = 0; i < is.length; i++) res[is[i]] = n.get(i);
    }
    return Arr.createL(res, o.shape);
  }
}