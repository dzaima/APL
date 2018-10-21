package APL.types.functions.builtins.mops;

import APL.Scope;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.*;

import java.util.*;

public class KeyBuiltin extends Mop {
  public KeyBuiltin(Scope sc) {
    super("⌸");
    valid = 0x011;
    this.sc = sc;
  }
  
  public Obj call(Obj aa, Value w) {
    if (aa instanceof APLMap) {
      if (w.rank > 1) {
        Value[] arr = new Value[w.ia];
        for (int i = 0; i < w.arr.length; i++) {
          arr[i] = (Value) ((APLMap) aa).getRaw(w.arr[i]);
        }
        return new Arr(arr, w.shape);
      }
      return ((APLMap) aa).getRaw(w);
    }
    if (aa instanceof Fun) {
      int i = ((Value)sc.get("⎕IO")).toInt(this);
      var vals = new HashMap<Value, ArrayList<Value>>();
      var order = new ArrayList<Value>();
      for (Value v : w.arr) {
        if (!vals.containsKey(v)) {
          var l = new ArrayList<Value>();
          l.add(new Num(i));
          vals.put(v, l);
          order.add(v);
        } else {
          vals.get(v).add(new Num(i));
        }
        i++;
      }
      var res = new Value[order.size()];
      i = 0;
      for (var c : order) {
        res[i++] = (Value) ((Fun)aa).call(c, new Arr(vals.get(c)));
      }
      return new Arr(res);
    }
    throw new DomainError("⌸ ⍶ not map nor function");
  }
  
  public Obj call(Obj aa, Value a, Value w) {
    if (aa instanceof APLMap) {
      ((APLMap)aa).set(a, w);
      return w;
    }
    throw new NYIError("dyadic ⌸"); // TODO
  }
}