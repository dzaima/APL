package APL.types;

import APL.Main;

public abstract class APLMap extends Value {
  APLMap() { }
  
  public MapPointer get(Value k) {
    return new MapPointer(this, k);
  }
  
  public abstract Obj getRaw(Value k);
  
  abstract public void set(Value k, Obj v);
  
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
      if (Main.debug) return v == null? "map@"+k : "ptr:"+v;
      return v == null? "map@"+k : v.toString();
    }
  }
}