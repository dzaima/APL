package APL.types;

import java.util.*;

public class StrMap extends APLMap {
  private HashMap<String, Obj> map = new HashMap<>();
  
  
  @Override
  public Obj getRaw(Value k) {
    Obj v = map.get(k.fromAPL());
    if (v == null) return Null.NULL;
    return v;
  }
  
  @Override
  public void set(Value k, Obj v) {
    map.put(k.fromAPL(), v);
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
