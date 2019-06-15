class MouseButton extends SimpleMap {
  int m;
  Num sX = Num.ZERO, sY = Num.ZERO;
  Arr sPos = new DoubleArr(new double[]{0, 0});
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
  void setv(String k, Obj v) {
    String s = k.toLowerCase();
    switch (s) {
      // callbacks
      case "clicked" : case "c": cH = (Fun) v; break;
      case "released": case "r": rH = (Fun) v; break;
      default: throw new DomainError("setting non-existing key "+s+" for Mouse");
    }
  }
  Obj getv(String k) {
    String s = k.toLowerCase();
    switch (s) {
      case "sx": case "startx": return sX;
      case "sy": case "starty": return sY;
      case "sp": case "s": case "startpos": return sPos;
      case "p": case "pressed": return pressed;
      case "pp": case "ppressed": return ppressed;
      default: return NULL;
    }
  }
  String toString() { return m==LEFT?"P5.lm":m==RIGHT?"P5.rm":"P5.mm"; }
}
