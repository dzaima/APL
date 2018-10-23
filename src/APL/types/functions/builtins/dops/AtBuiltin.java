package APL.types.functions.builtins.dops;

import APL.*;
import APL.types.*;
import APL.types.functions.*;

public class AtBuiltin extends Dop {
  public AtBuiltin(Scope sc) {
    super("@", 0x001, sc);
  }
  public Obj call(Obj aa, Obj ww, Value w) {
    int IO = ((Num)sc.get("⎕IO")).intValue();
    int ia = w.ia;
    if (ww instanceof Fun) {
      Value vba = (Value) ((Fun) ww).call(w);
      boolean[] ba = new boolean[ia];
      int matchingCount = 0;
      for (int i = 0; i < ia; i++) {
        ba[i] = Main.bool(vba.arr[i], sc);
        if (ba[i]) matchingCount++;
      }
      Value aaa;
      if (aa instanceof Fun) {
        Value[] matching = new Value[matchingCount];
        int ptr = 0;
        for (int i = 0; i < ia; i++) {
          if (ba[i]) matching[ptr++] = w.arr[i];
        }
        aaa = (Value) ((Fun) aa).call(new Arr(matching));
      } else aaa = (Value) aa;
      Value[] ra = new Value[ia];
      if (aaa.rank == 0) {
        Value inner = aaa.arr[0];
        for (int i = 0; i < ia; i++) {
          if (ba[i]) ra[i] = inner;
          else ra[i] = w.arr[i];
        }
      } else {
        int ptr = 0;
        for (int i = 0; i < ia; i++) {
          if (ba[i]) ra[i] = aaa.arr[ptr++];
          else ra[i] = w.arr[i];
        }
      }
      return new Arr(ra, w.shape);
    } else {
      Value wwa = (Value) ww;
      int matchingCount = wwa.ia;
      Value[] ra = new Value[ia];
      int[] indexes = new int[wwa.ia];
      if (aa instanceof Fun) {
        Value[] matching = new Value[matchingCount];
        for (int i = 0; i < matchingCount; i++) {
          indexes[i] = Indexer.fromShape(w.shape, wwa.arr[i].toIntArr(this), IO);
          matching[i] = w.arr[indexes[i]];
        }
        Value[] replacement = ((Value) ((Fun) aa).call(new Arr(matching))).arr;
        System.arraycopy(w.arr, 0, ra, 0, ia);
        for (int i = 0; i < matchingCount; i++) {
          ra[indexes[i]] = replacement[i];
        }
        return new Arr(ra, w.shape);
      } else {
        for (int i = 0; i < matchingCount; i++) {
          indexes[i] = Indexer.fromShape(w.shape, wwa.arr[i].toIntArr(this), IO);
        }
        Value aaa = (Value) aa;
        System.arraycopy(w.arr, 0, ra, 0, ia);
        if (aaa.rank == 0) {
          Value inner = aaa.arr[0];
          for (int i = 0; i < matchingCount; i++) {
            ra[indexes[i]] = inner;
          }
        } else {
          for (int i = 0; i < matchingCount; i++) {
            ra[indexes[i]] = aaa.arr[i];
          }
        }
        return new Arr(ra, w.shape);
      }
    }
  }
}