package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.*;
import APL.types.*;

public class RShoeBuiltin extends Fun {
  @Override public String repr() {
    return "⊃";
  }
  
  public RShoeBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    if (w instanceof Primitive) return w;
    else if (w.ia == 0) throw new DomainError("⊃ on array with 0 elements", this, w);
    else return w.first();
  }
  
  public Value call(Value a, Value w) {
    Obj o = callObj(a, w);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Value a, Value w) {
    if (w instanceof APLMap) {
      APLMap map = (APLMap) w;
      return map.getRaw(a);
    }
    if (a instanceof Num) {
      if (w.rank != 1) throw new RankError("array rank was "+w.rank+", tried to get item at rank 0", w);
      if (w.ia == 0) throw new LengthError("⊃ on array with 0 elements", this, w);
      int p = a.asInt() - sc.IO;
      if (p >= w.ia) throw new DomainError("Tried to access item at position "+a+" while shape was "+(w.ia-1));
      return w.get(p);
    }
    for (Value v : a) {
      w = w.at(v.asIntVec(), sc.IO);
    }
    return w;
  }
  
  public Value under(Obj o, Value w) {
    Value[] vs = w.valuesCopy();
    vs[0] = o instanceof Fun? ((Fun) o).call(call(w)) : (Value) o;
    return Arr.createL(vs, w.shape);
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(a, w)) : (Value) o;
    if (a instanceof Primitive) {
      Value[] vs = w.valuesCopy();
      vs[a.asInt() - sc.IO] = v;
      return Arr.createL(vs, w.shape);
    } else {
      Value[] vs = w.valuesCopy();
      int[] is = a.asIntVec();
      replace(vs, v, is, 0);
      return Arr.createL(vs, w.shape);
    }
  }
  private void replace(Value[] vs, Value w, int[] d, int i) {
    int c = d[i]-sc.IO;
    if (i+1 == d.length) vs[c] = w;
    else {
      Value cv = vs[c];
      Value[] vsN = cv.valuesCopy();
      replace(vsN, w, d, i+1);
      vs[c] = Arr.createL(vsN, cv.shape);
    }
  }
}