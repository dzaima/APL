package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.ArrayList;


public class EpsilonBuiltin extends Builtin {
  @Override public String repr() {
    return "∊";
  }
  
  
  
  public Obj call(Value w) {
    var res = new ArrayList<Value>();
    rec(res, w);
    return Arr.create(res.toArray(new Value[0]));
  }
  
  private void rec(ArrayList<Value> arr, Value v) {
    if (v instanceof Primitive) {
      arr.add(v);
    } else {
      for (Value c : v) rec(arr, c);
    }
  }
  
  public Obj call(Value a, Value w) {
    if (a.scalar()) {
      Value a1 = a.first();
      for (Value v : w) {
        if (v.equals(a1)) {
          return Num.ONE;
        }
      }
      return Num.ZERO;
    }
    Value[] res = new Value[a.ia];
    for (int i = 0; i < a.ia; i++) {
      Value av = a.get(i);
      Num b = Num.ZERO;
      for (Value v : w) {
        if (v.equals(av)) {
          b = Num.ONE;
          break;
        }
      }
      res[i] = b;
    }
    return Arr.create(res, a.shape);
  }
  
  
  public boolean strInv() { return true; }
  public Value strInv(Value w, Value origW) {
    Value[] vs = w.values();
    Value[] res = new Value[origW.ia];
    int e = copyIn(res, vs, origW, 0);
    if (e != w.ia) throw new DomainError("⍢∊ expected equal amount of output & output items", this);
    return Arr.create(res, origW.shape);
  }
  private int copyIn(Value[] res, Value[] vs, Value orig, int s) {
    for (int i = 0; i < orig.ia; i++) {
      Value origN = orig.get(i);
      if (origN instanceof Primitive) {
        res[i] = vs[s++];
      } else {
        Value[] resN = new Value[origN.ia];
        s = copyIn(resN, vs, origN, s);
        res[i] = Arr.create(resN, origN.shape);
      }
    }
    return s;
  }
}