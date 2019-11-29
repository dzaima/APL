package APL.types.functions.builtins.mops;

import APL.Scope;
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
  
  public Obj call(Obj aa, Value w, DerivedMop derv) {
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
      Num IOv = Num.of(i);
      var vals = new HashMap<Value, ArrayList<Value>>();
      var order = new ArrayList<Value>();
      for (Value v : w) {
        if (!vals.containsKey(v)) {
          var l = new ArrayList<Value>();
          l.add(IOv);
          vals.put(v, l);
          order.add(v);
        } else {
          vals.get(v).add(IOv);
        }
        i++;
      }
      var res = new Value[order.size()];
      i = 0;
      for (var c : order) {
        res[i++] = (Value) ((Fun)aa).call(c, Arr.create(vals.get(c).toArray(new Value[0])));
      }
      return new HArr(res);
    }
    throw new DomainError("⌸ ⍶ not map nor function");
  }
  
  public Obj call(Obj aa, Value a, Value w, DerivedMop derv) {
    if (aa instanceof APLMap) {
      ((APLMap)aa).set(a, w);
      return w;
    }
    throw new NYIError("dyadic ⌸", this); // TODO
  }
}