class APLImg extends APLMap {
  PImage img;
  APLImg(PImage img) {
    this.img = img;
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
      case "w": case "width" : return new Num(img.width );
      case "h": case "height": return new Num(img.height);
      default: return NULL;
    }
  }
  int size() { throw new SyntaxError("Getting size of the APLImg object"); }
  String toString() { return "APLImg"; }
}
