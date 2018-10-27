package APL.types.functions;

import APL.*;
import APL.types.*;
import APL.types.arrs.HArr;

import java.util.*;

public class VarArr extends Obj {
  public final ArrayList<Obj> arr;
  public final int ia;
  public VarArr(ArrayList<Obj> arr) {
    ia = arr.size();
    Collections.reverse(arr);
    this.arr = arr;
  }
  
  public HArr materialize() {
    Value[] res = new Value[arr.size()];
    for (int i = 0; i < ia; i++) {
      Obj c = arr.get(i);
      res[i] = c instanceof VarArr? ((VarArr) c).materialize() : (Value) (c instanceof Value? c : ((Settable) c).get());
    }
    return new HArr(res);
  }
  
  @Override
  public Type type() {
    return Type.array;
  }
  
  @Override
  public String toString() {
    if (Main.debug) return "vararr:"+arr;
    return materialize().toString();
  }
}
