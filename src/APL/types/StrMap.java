package APL.types;

import java.util.*;

public class StrMap extends APLMap {
  private final HashMap<String, Obj> map = new HashMap<>();
  
  
  @Override
  public Obj getRaw(Value k) {
    return getRaw(k.fromAPL());
  }
  @Override
  public Obj getRaw(String k) {
    Obj v = map.get(k);
    if (v == null) return Null.NULL;
    return v;
  }
  
  @Override
  public void set(Value k, Obj v) {
    if (v == Null.NULL) map.remove(k.fromAPL());
    else map.put(k.fromAPL(), v);
  }
  
  @Override
  public Arr toArr() {
    Object[] a = map.values().toArray();
    var items = new ArrayList<Value>();
    for (Object o : a) {
      if (o instanceof Value) items.add((Value) o);
    }
    return new Arr(items);
  }
  
  @Override
  public int size() {
    return map.size();
  }
  
  @Override
  public boolean equals(Obj o) {
    return o instanceof StrMap && map.equals(((StrMap) o).map);
  }
  
  @Override
  public String toString() {
    StringBuilder res = new StringBuilder("(");
    map.forEach((key, value) -> {
      if (res.length() != 1) res.append("⋄");
      res.append(key).append(":").append(value);
    });
    return res + ")";
  }
}
