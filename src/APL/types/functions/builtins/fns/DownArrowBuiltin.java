package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.types.functions.Builtin;
import APL.types.*;

public class DownArrowBuiltin extends Builtin {
  public DownArrowBuiltin(Scope sc) {
    super("↓");
    valid = 0x011;
    this.sc = sc;
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