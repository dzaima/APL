package APL.types.functions.builtins.mops;

import APL.*;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.HArr;
import APL.types.functions.*;

import java.util.*;

public class KeyBuiltin extends Mop {
  @Override public String repr() {
    return "⌸";
  }
  
  public KeyBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Obj f, Value w, DerivedMop derv) {
    Obj o = callObj(f, w, derv);
    if (o instanceof Value) return (Value) o;
    throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
  }
  public Obj callObj(Obj aa, Value w, DerivedMop derv) {
    if (aa instanceof APLMap) {
      if (w.rank > 1) {
        Value[] arr = new Value[w.ia];
        for (int i = 0; i < w.ia; i++) {
          arr[i] = (Value) ((APLMap) aa).getRaw(w.get(i));
        }
        return Arr.create(arr, w.shape);
      }
      return ((APLMap) aa).getRaw(w);
    }
    if (aa instanceof Fun) {
      int i = sc.IO;
      var vals = new HashMap<Value, ArrayList<Value>>();
      var order = new ArrayList<Value>();
      for (Value v : w) {
        if (!vals.containsKey(v)) {
          var l = new ArrayList<Value>();
          l.add(Num.of(i));
          vals.put(v, l);
          order.add(v);
        } else {
          vals.get(v).add(Num.of(i));
        }
        i++;
      }
      var res = new Value[order.size()];
      i = 0;
      for (Value c : order) {
        res[i++] = ((Fun)aa).call(c, Arr.create(vals.get(c).toArray(new Value[0])));
      }
      return new HArr(res);
    }
    throw new DomainError("⌸: ⍶ must be a function or a map, was "+aa.humanType(true), derv, aa);
  }
  
  public Value call(Obj aa, Value a, Value w, DerivedMop derv) {
    if (aa instanceof APLMap) {
      ((APLMap)aa).set(a, w);
      return w;
    }
    if (aa instanceof Fun) {
      Fun aaf = (Fun) aa;
      if (!Arrays.equals(a.shape, w.shape)) {
        if (a.rank != w.rank) throw new RankError("dyadic ⌸ expected ⍺ & ⍵ to have equal ranks ("+a.rank+" vs "+w.rank+")", derv, w);
        throw new LengthError("dyadic ⌸ expected ⍺ & ⍵ to have equal shapes ("+Main.toAPL(a.shape)+" vs "+ Main.toAPL(w.shape)+")", derv, w);
      }
      HashMap<Value, ArrayList<Value>> vals = new HashMap<>();
      ArrayList<Value> order = new ArrayList<>();
      for (int i = 0; i < a.ia; i++) {
        Value k = w.get(i);
        Value v = a.get(i);
        ArrayList<Value> curr = vals.get(k);
        if (curr == null) {
          ArrayList<Value> newArr = new ArrayList<>();
          vals.put(k, newArr);
          curr = newArr;
          order.add(k);
        }
        curr.add(v);
      }
      Value[] res = new Value[order.size()];
      for (int i = 0; i < order.size(); i++) {
        Value k = order.get(i);
        Value vs = Arr.create(vals.get(k).toArray(new Value[0])); // TODO make an ArrayList HArr?
        res[i] = aaf.call(k, vs);
      }
      return Arr.create(res);
    }
    throw new DomainError("⌸: ⍶ must be a function or a map, was "+aa.humanType(true), derv, aa); 
  }
}