class APLImg extends SimpleMap {
  PImage img;
  Arr size, px;
  Num w, h;
  APLImg(PImage img) {
    this.img = img;
    w = new Num(img.width );
    h = new Num(img.height);
    size = new DoubleArr(new double[]{img.width, img.height});
  }
  
  void setv(String k, Obj v) {
    String s = k.toLowerCase();
    switch (s) {
      default: throw new DomainError("setting non-existing key "+s+" for APLImg");
    }
  }
  Obj getv(String k) {
    String s = k.toLowerCase();
    switch (s) {
      case "w": case "width" : return w;
      case "h": case "height": return h;
      case "dimensions": case "size": case "sz": return size;
      case "mat": case "pixels": 
        if (px != null) return px;
        int ia = img.pixels.length;
        double[] vals = new double[ia];
        for (int i = 0; i < ia; i++) {
          vals[i] = img.pixels[i];
        }
        return new DoubleArr(vals, new int[]{img.width, img.height});
      default: return NULL;
    }
  }
  String toString() { return "APLImg"; }
}
