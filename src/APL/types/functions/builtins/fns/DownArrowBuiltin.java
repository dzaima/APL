package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.types.functions.Builtin;
import APL.types.*;

public class DownArrowBuiltin extends Builtin {
  public DownArrowBuiltin(Scope sc) {
    super("↓", 0x011, sc);
  }
  
  @Override
  public Obj call(Value w) {
    if (!(w instanceof Arr)) return w;
    if (w.rank <= 1) return new Arr(w);
    // TODO stupid dimensions
    int csz = w.shape[w.rank-1]; // chunk size
    int cam = w.ia/csz; // chunk amount
    Value[] res = new Value[cam];
    for (int i = 0; i < cam; i++) {
      Value[] c = new Value[csz];
      for (int j = 0; j < csz; j++) {
        c[j] = w.arr[i*csz + j];
      }
      res[i] = new Arr(c);
    }
    int[] nsh = new int[w.rank-1];
    System.arraycopy(w.shape, 0, nsh, 0, nsh.length);
    return new Arr(res, nsh);
  }
  
  public Obj call(Value a, Value w) { // TODO ⍴⍴⍺ < ⍴⍴⍵
    int IO = ((Value)sc.get("⎕IO")).toInt(this);
    int[] shape = a.toIntArr(this);
    if (shape.length == 0) return w;
    int ia = 1;
    int[] offsets = new int[shape.length];
    for (int i = 0; i < shape.length; i++) {
      int d = shape[i];
      if (d < 0) {
        d = -d;
        offsets[i] = IO;
      } else {
        offsets[i] = d + IO;
      }
      shape[i] = w.shape[i] - d;
      ia *= shape[i];
    }
    Value[] arr = new Value[ia];
    Indexer indexer = new Indexer(shape, offsets);
    int i = 0;
    for (int[] index : indexer) {
      arr[i] = w.at(index, this);
      i++;
    }
    return new Arr(arr, shape);
  }
}