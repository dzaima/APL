class MouseButton extends APLMap {
  Arr toArr() {
    throw new SyntaxError("Converting the P5 object to array");
  }
  int m;
  Num sX = Num.ZERO, sY = Num.ZERO;
  Arr sPos = new Arr(new Value[]{Num.ZERO, Num.ZERO});
  Num pressed = Num.ZERO;
  Num ppressed = Num.ZERO;
  Fun cH, rH;
  MouseButton(int id) {
    m = id;
  }
  void draw() {
    ppressed = pressed;
    boolean p = mousePressed && mouseButton == m;
    pressed = p? Num.ONE : Num.ZERO;
    if (ppressed == Num.ZERO && p) {
      sX = mx;
      sY = my;
      sPos = mpos;
      call(cH, mpos);
    }
    else if (ppressed == Num.ONE && !p) {
      call(rH, sPos, mpos);
    }
  }
  void set(Value k, Obj v) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      // callbacks
      case "clicked" : case "c": cH = (Fun) v; break;
      case "released": case "r": rH = (Fun) v; break;
      default: throw new DomainError("setting non-existing key "+s+" for Mouse");
    }
  }
  Obj getRaw(Value k) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      case "sx": case "startx": return sX;
      case "sy": case "starty": return sY;
      case "sp": case "s": case "startpos": return sPos;
      case "p": case "pressed": return pressed;
      case "pp": case "ppressed": return ppressed;
      default: return NULL;
    }
  }
  int size() {
    throw new SyntaxError("Getting size of the P5 object");
  }
  String toString() { return m==LEFT?"P5.lm":m==RIGHT?"P5.rm":"P5.mm"; }
}
