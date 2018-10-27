class XY {
  double x,y;
  XY(double xi, double yi) {
    x = xi;
    y = yi;
  }
  XY(Obj a) {
    int[] d = ((Value) a).asIntArr();
    if (d.length != 2) throw new LengthError("argument for P5.size must be length 2");
    x = d[0];
    y = d[1];
  }
  XY(Value xi, Value yi) {
    x = xi.asInt();
    y = yi.asInt();
  }
}
int col(Obj v) {
  if (v instanceof Num) return ((Num) v).asInt();
  String s = ((Value) v).asString();
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
  if (v.ia==0) return emptyF2D;
  if (v.get(0) instanceof Num) v = new Shape1Arr(v);
  float[][] res = new float[v.ia][];
  for (int i = 0; i < res.length; i++) {
    Value c = v.get(i);
    float[] cr = new float[c.ia];
    if (c.quickDoubleArr()) {
      double[] da = c.asDoubleArr();
      for (int j = 0; j < cr.length; j++) cr[j] = (float) da[j];
    } else {
      for (int j = 0; j < cr.length; j++) cr[j] = (float)(c.get(j)).asDouble();
    }
    res[i] = cr;
  }
  return res;
}
float[] f1D(Value v) {
  float[] res = new float[v.ia];
  if (v.quickDoubleArr()) {
    double[] da = v.asDoubleArr();
    for (int j = 0; j < res.length; j++) res[j] = (float) da[j];
  } else {
    for (int j = 0; j < res.length; j++) res[j] = (float)(v.get(j)).asDouble();
  }
  return res;
}

Arr arr (int... ia) {
  return Main.toAPL(ia);
}
Arr arr (boolean... ia) {
  double[] vs = new double[ia.length]; // TODO make Main.toBoolArr
  for (int i = 0; i < ia.length; i++) vs[i] = ia[i]? 1 : 0;
  return new DoubleArr(vs);
}

Arr APL(byte[] a) {
  double[] res = new double[a.length];
  for (int i = 0; i < a.length; i++) res[i] = a[i]&0xff;
  return new DoubleArr(res);
}
Arr APL(String[] a) {
  Value[] res = new Value[a.length];
  for (int i = 0; i < a.length; i++) res[i] = Main.toAPL(a[i]);
  return new HArr(res);
}
