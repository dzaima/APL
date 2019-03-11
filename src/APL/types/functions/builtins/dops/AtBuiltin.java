package APL.types.functions.builtins.dops;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

public class AtBuiltin extends Dop {
  @Override public String repr() {
    return "@";
  }
  
  public AtBuiltin(Scope sc) {
    super(sc);
  }
  public Obj call(Obj aa, Obj ww, Value w, DerivedDop derv) {
    int IO = sc.IO;
    int ia = w.ia;
    if (ww instanceof Fun) {
      Value vba = (Value) ((Fun) ww).call(w);
      boolean[] ba = new boolean[ia];
      int matchingCount = 0;
      for (int i = 0; i < ia; i++) {
        ba[i] = Main.bool(vba.get(i), sc);
        if (ba[i]) matchingCount++;
      }
      Value aaa;
      if (aa instanceof Fun) {
        Value[] matching = new Value[matchingCount];
        int ptr = 0;
        for (int i = 0; i < ia; i++) {
          if (ba[i]) matching[ptr++] = w.get(i);
        }
        aaa = (Value) ((Fun) aa).call(Arr.create(matching));
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
      if (w.rank == 0 && ra[0] instanceof Primitive) return ra[0];
      return Arr.create(ra, w.shape);
    } else {
      Value wwa = (Value) ww;
      int matchingCount = wwa.ia;
      Value[] ra = new Value[ia];
      int[] indexes = new int[wwa.ia];
      if (aa instanceof Fun) {
        Value[] matching = new Value[matchingCount];
        for (int i = 0; i < matchingCount; i++) {
          indexes[i] = Indexer.fromShape(w.shape, wwa.get(i).asIntVec(), IO);
          matching[i] = w.get(indexes[i]);
        }
        Value[] replacement = ((Value) ((Fun) aa).call(Arr.create(matching))).values();
        System.arraycopy(w.values(), 0, ra, 0, ia);
        for (int i = 0; i < matchingCount; i++) {
          ra[indexes[i]] = replacement[i];
        }
        return Arr.create(ra, w.shape);
      } else {
        for (int i = 0; i < matchingCount; i++) {
          indexes[i] = Indexer.fromShape(w.shape, wwa.get(i).asIntVec(), IO);
        }
        Value aaa = (Value) aa;
        System.arraycopy(w.values(), 0, ra, 0, ia);
        if (aaa.rank == 0) {
          Value inner = aaa.get(0);
          for (int i = 0; i < matchingCount; i++) {
            ra[indexes[i]] = inner;
          }
        } else {
          for (int i = 0; i < matchingCount; i++) {
            ra[indexes[i]] = aaa.get(i);
          }
        }
        if (w.rank == 0 && ra[0] instanceof Primitive) return ra[0];
        return Arr.create(ra, w.shape);
      }
    }
  }
}