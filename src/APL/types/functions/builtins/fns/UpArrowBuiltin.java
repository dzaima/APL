package APL.types.functions.builtins.fns;

import APL.Indexer;
import APL.Scope;
import APL.types.functions.Builtin;
import APL.types.*;

import java.util.Arrays;

public class UpArrowBuiltin extends Builtin {
  public UpArrowBuiltin(Scope sc) {
    super("↑");
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
      ia *= d;
      if (d < 0) {
        shape[i] = -d;
        ia = -ia;
        offsets[i] = w.shape[i]-shape[i]+IO;
      } else offsets[i] = IO;
    }
    Value[] arr = new Value[ia];
    Indexer indexer = new Indexer(shape, offsets);
    int i = 0;
    for (int[] index : indexer) {System.out.println(Arrays.toString(index));
      arr[i] = w.at(index, this);
      i++;
    }
    return new Arr(arr, shape);
  }
}