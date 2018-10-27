class APLImg extends APLMap {
  PImage img;
  Arr size, px;
  Num w, h;
  APLImg(PImage img) {
    this.img = img;
    w = new Num(img.width );
    h = new Num(img.height);
    size = new Arr(new Value[]{w, h});
  }
  
  Arr toArr() { throw new SyntaxError("Converting a APLImg object to array"); }
  void set(Value k, Obj v) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      default: throw new DomainError("setting non-existing key "+s+" for APLImg");
    }
  }
  Obj getRaw(Value k) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      case "w": case "width" : return w;
      case "h": case "height": return h;
      case "dimensions": case "size": case "sz": return size;
      case "mat": case "pixels": 
        if (px != null) return px;
        int ia = img.pixels.length;
        Value[] vals = new Value[ia];
        for (int i = 0; i < ia; i++) {
          vals[i] = new Num(img.pixels[i]);
        }
        return new Arr(vals, new int[]{img.width, img.height});
      default: return NULL;
    }
  }
  int size() { throw new SyntaxError("Getting size of the APLImg object"); }
  String toString() { return "APLImg"; }
}
