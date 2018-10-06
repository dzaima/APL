package APL.types.functions;

import APL.*;
import APL.types.*;

import java.util.*;

public class VarArr extends Obj {
  public ArrayList<Obj> arr;
  public int ia;
  public VarArr(ArrayList<Obj> arr) {
    ia = arr.size();
    Collections.reverse(arr);
    this.arr = arr;
  }
  
  public Arr materialize() {
    Value[] res = new Value[arr.size()];
    for (int i = 0; i < ia; i++) {
      Obj c = arr.get(i);
      res[i] = c instanceof VarArr? ((VarArr)c).materialize() : (Value) (c instanceof Value? c : ((Variable)c).get());
    }
    return new Arr(res);
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
