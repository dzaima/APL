class XY {
  double x,y;
  XY(double xi, double yi) {
    x = xi;
    y = yi;
  }
  XY(Obj a) {
    int[] d = ((Value) a).toIntArr(tf);
    if (d.length != 2) throw new LengthError("argument for P5.size must be length 2");
    x = d[0];
    y = d[1];
  }
  XY(Value xi, Value yi) {
    x = xi.toInt(tf);
    y = yi.toInt(tf);
  }
}
int col(Obj v) {
  if (v instanceof Num) return ((Num) v).toInt(tf);
  String s = ((Value) v).fromAPL();
  if (s.length() == 1) {
    int i = Integer.parseInt(s, 16);
    return i | (i<<4) | (i<<8) | (i<<12) | (i<<16) | (i<<20) | 0xff000000;
  }
  if (s.length() == 6) return Integer.parseInt(s, 16) | 0xff000000;
  if (s.length() == 8) return Integer.parseInt(s, 16);
  if (s.length() == 2) {
    int i = Integer.parseInt(s, 16);
    return i | (i<<8) | (i<<16) | 0xff000000;
  }
  if (s.length() == 3) {
    int i = Integer.parseInt(s, 16);
    return ((i&0xf)*17) | ((i&0xf0)*17 << 4) | ((i&0xf00)*17 << 8) | 0xff000000;
  }
  throw new DomainError("bad color "+v);
}

abstract class ForFA extends Fun {
  
  ForFA() { super(0x011); }
  
  abstract void draw(float[] fa);
  void setup(Value a) { }
  void finish() { }
  //public Obj call(Value a, Value w) {
  //  XY p1 = new XY(a);
  //  XY p2 = new XY(w);
  //  draw(p1, p2);
  //  return w;
  //}
  Value w;
  public Obj call(Value w) { // TODO input is 2D arr
    this.w = w;
    setup(null);
    for (float[] fa : f2D(w)) draw(fa);
    finish();
    return w;
  }
  public Obj call(Value a, Value w) { // TODO input is 2D arr
    setup(a);
    for (float[] fa : f2D(w)) draw(fa);
    finish();
    return w;
  }
}

float[][] emptyF2D = new float[0][0];

float[][] f2D(Value v) {
  if (v.arr.length==0) return emptyF2D;
  if (v.arr[0] instanceof Num) v = new Arr(new Value[]{v});
  float[][] res = new float[v.ia][];
  for (int i = 0; i < res.length; i++) {
    Value c = v.arr[i];
    float[] cr = new float[c.ia];
    for (int j = 0; j < cr.length; j++) cr[j] = (float)((Num)(c.arr[j])).doubleValue();
    res[i] = cr;
  }
  return res;
}
float[] f1D(Value v) {
  float[] res = new float[v.ia];
  for (int i = 0; i < res.length; i++) {
    res[i] = (float)((Num)(v.arr[i])).doubleValue();
  }
  return res;
}

Arr arr (int... ia) {
  return Main.toAPL(ia);
}
Arr arr (boolean... ia) {
  Value[] vs = new Value[ia.length]; // TODO make Main.toBoolArr
  for (int i = 0; i < ia.length; i++) vs[i] = ia[i]? Num.ONE : Num.ZERO;
  return new Arr(vs);
}

Arr APL(byte[] a) {
  Value[] res = new Value[a.length];
  for (int i = 0; i < a.length; i++) res[i] = new Num(a[i]&0xff);
  return new Arr(res);
}
Arr APL(String[] a) {
  Value[] res = new Value[a.length];
  for (int i = 0; i < a.length; i++) res[i] = Main.toAPL(a[i], null);
  return new Arr(res);
}
