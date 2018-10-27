package APL.types;

import APL.Main;
import APL.types.arrs.HArr;

public abstract class APLMap extends Primitive {
  
  public MapPointer get(Value k) {
    return new MapPointer(this, k);
  }
  
  public abstract Obj getRaw(Value k);
  
  public Obj getRaw(String k) {
    return getRaw(Main.toAPL(k));
  }
  public MapPointer get(String k) {
    return get(Main.toAPL(k));
  }
  
  abstract public void set(Value k, Obj v);
  
  abstract public HArr toArr();
  
  abstract public int size();
  
  class MapPointer extends Settable {
    private final APLMap map;
    private final Value k;
    
    MapPointer(APLMap map, Value k) {
      super(map.getRaw(k));
      this.map = map;
      this.k = k;
    }
    
    @Override
    public void set(Obj v) {
      map.set(k, v);
    }
  
    @Override
    public String toString() {
      if (Main.debug) return v == null? "map@"+k : "ptr@"+k+":"+v;
      return v == null? "map@"+k : v.toString();
    }
  }
  
  @Override
  public Value ofShape(int[] sh) {
    int ia = 1;
    for (int c : sh) ia*= c;
    Value[] vs = new Value[ia];
    for (int i = 0; i < ia; i++) vs[i] = this;
    return new HArr(vs, sh);
  }
}