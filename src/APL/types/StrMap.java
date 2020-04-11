package APL.types;

import APL.*;
import APL.types.arrs.HArr;

import java.util.*;

public class StrMap extends APLMap {
  public final HashMap<String, Obj> vals;
  // public final Scope sc;
  
  public StrMap(Scope sc) {
    this.vals = sc.vars;
    // this.sc = sc;
  }
  
  public StrMap(HashMap<String, Obj> vals) {
    this.vals = vals;
  }
  
  public StrMap() {
    this.vals = new HashMap<>();
    // this.sc = null;
  }
  
  // public StrMap(Scope sc, HashMap<String, Obj> vals) {
  //   this.sc = sc;
  //   this.vals = vals;
  // }
  
  
  @Override
  public Obj getRaw(Value k) {
    return getRaw(k.asString());
  }
  @Override
  public Obj getRaw(String k) {
    Obj v = vals.get(k);
    if (v == null) return Null.NULL;
    return v;
  }
  
  @Override
  public void set(Value k, Obj v) {
    if (v == Null.NULL) vals.remove(k.asString());
    else vals.put(k.asString(), v);
  }
  
  public void setStr(String k, Obj v) {
    if (v == Null.NULL) vals.remove(k);
    else vals.put(k, v);
  }
  
  @Override
  public Arr allValues() {
    var items = new ArrayList<Value>();
    for (Obj o : vals.values()) {
      if (o instanceof Value) items.add((Value) o);
    }
    return Arr.create(items);
  }
  
  @Override public Arr allKeys() {
    var items = new ArrayList<Value>();
    for (String o : vals.keySet()) {
      items.add(Main.toAPL(o));
    }
    return Arr.create(items);
  }
  
  @Override public Arr kvPair() {
    ArrayList<Value> ks = new ArrayList<>();
    ArrayList<Value> vs = new ArrayList<>();
    vals.forEach((k, v) -> {
      if (v instanceof Value) {
        ks.add(Main.toAPL(k));
        vs.add((Value) v);
      }
    });
    return new HArr(new Value[]{
      HArr.create(ks),
       Arr.create(vs)});
  }
  
  @Override
  public int size() {
    return vals.size();
  }
  
  @Override
  public boolean equals(Obj o) {
    return o instanceof StrMap && vals.equals(((StrMap) o).vals);
  }
  
  @Override
  public String toString() {
    StringBuilder res = new StringBuilder("(");
    vals.forEach((key, value) -> {
      if (res.length() != 1) res.append(" ⋄ ");
      res.append(key).append(":").append(value);
    });
    return res + ")";
  }
}