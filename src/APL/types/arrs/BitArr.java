package APL.types.arrs;

import APL.*;
import APL.errors.*;
import APL.types.*;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class BitArr extends Arr {
  
  public final long[] arr;
  // data[0]&1 - 1st item, (data[0]&0b10)
  // filler can be anything
  
  public BitArr(long[] arr, int[] shape) {
    super(shape);
    assert sizeof(shape) == arr.length : arr.length+" not expected for shape "+Main.formatAPL(shape);
    this.arr = arr;
  }
  
  public BitArr(long[] arr, int[] shape, int ia) {
    super(shape, ia);
    this.arr = arr;
  }
  
  public static BitArr of(Arr a) {
    if (a instanceof BitArr) return (BitArr) a;
    if (a.quickDoubleArr()) {
      double[] ds = a.asDoubleArr();
      long[] arr = convert(ds);
      return new BitArr(arr, a.shape);
    }
    long[] arr = new long[(a.ia+63 >> 6)];
    int p = 0;
    for(Value v : a) {
      int n = Main.bool(v)? 1 : 0;
      arr[p>>6] = arr[p>>6]  |  n << (p&63);
    }
    return new BitArr(arr, a.shape);
  }
  
  public static long[] convert(double[] arr) {
    long[] res = new long[arr.length+63 >> 6];
    for (int i = 0; i < arr.length; i++) {
      double d = arr[i];
      if (d != 0 && d != 1) throw new DomainError("Converting " + d + " to boolean");
      res[i>>6] = res[i>>6]  |  (int)d << (i&63);
    }
    return res;
  }
  
  public static int sizeof(Value w) {
    return w.ia+63 >> 6;
  }
  
  public static int sizeof(int[] sh) {
    int m = 1;
    for (int i : sh) m*= i;
    return sizeof(m);
  }
  public static int sizeof(int am) {
    return am+63 >> 6;
  }
  
  public static Value fill(Value v, boolean b) {
    long[] arr = new long[sizeof(v)];
    if (!b) return new BitArr(arr, v.shape, v.ia);
    Arrays.fill(arr, -1L);
    return new BitArr(arr, v.shape, v.ia);
  }
  
  @Override public int[] asIntArr() {
    int[] res = new int[ia];
    int ctr = 0;
    for (int i = 0; i < arr.length-1; i++) {
      long cl = arr[i];
      for (int j = 0; j < 64; j++) {
        res[ctr++] = (int) (cl&1);
        cl>>= 1;
      }
    }
    int over = ia & 63; // aka ia % 64
    for (int i = 0; i < over; i++) {
      res[ctr++] = (int) ((arr[ctr / 64]>>i) & 1);
    }
    return res;
  }
  
  @Override public double[] asDoubleArr() {
    double[] res = new double[ia];
    int ctr = 0;
    for (int i = 0; i < arr.length-1; i++) {
      long cl = arr[i];
      for (int j = 0; j < 64; j++) {
        res[ctr++] = cl&1;
        cl>>= 1;
      }
    }
    int over = ia & 63; // aka ia % 64
    for (int i = 0; i < over; i++) {
      res[ctr++] = (int) ((arr[ctr / 64]>>i) & 1);
    }
    return res;
  }
  
  @Override public int asInt() {
    throw new RankError("using bit arr as int", this);
  }
  
  @Override public Value get(int i) {
    return new Num((arr[i>>6] >> (i&63))  &  1);
  }
  
  @Override public String asString() {
    throw new DomainError("using bit arr as string");
  }
  
  @Override public Value prototype() {
    return Num.ZERO;
  }
  
  @Override public Value ofShape(int[] sh) {
    return new BitArr(arr, sh);
  }
  
  @Override public boolean quickDoubleArr() {
    return true;
  }
  
  public int llen() { // long length
    return arr.length;
  }
  public boolean extra() { // long length
    return (ia&63) != 0;
  }
  
  public void setEnd(boolean on) {
    if (extra()) {
      int extra = ia&63;
      long tail = -(1L<<extra); // bits outside of the array
      long last = arr[arr.length - 1]; // last item of the array
      long at = tail & (on? ~last : last); // masking tail bits of the last item
      arr[arr.length-1] = last ^ at;
    }
  }
  
  public double sum() {
    return isum();
  }
  
  public int isum() {
    int r = 0;
    setEnd(false);
    for (long l : arr) {
      r += Long.bitCount(l);
    }
    return r;
  }
  
  public static class BA { // boolean append
    long[] a;
    private int i, o = 0; // index, offset
    public BA(long[] a) {
      this.a = a;
    }
    public BA(int am) {
      this.a = new long[sizeof(am)];
    }
    public BA(long[] a, int start) {
      this.a = a;
      i = start>>6;
      o = start & 63;
    }
    public void append(boolean b) {
      a[i] |= (b? 1L : 0L)<<o;
      o++;
      // i+= o==64? 1 : 0; // todo, idk ._.
      // o&= 63;
      if (o == 64) {
        o = 0;
        i++;
      }
    }
  
    public void append(BitArr a) {
      append(a, 0, a.ia);
    }
    
    public void append(BitArr g, int s, int e) {
      // System.out.println(g.ia+" "+s+" "+e+" "+i+" "+o+" "+a.length);
      // a.setEnd(false);
      // ↓ too much work for rare speedup
      // if ((o-s & 63) == 0 && g.ia > 64) {
      //   int si = s>>6;
      //   int so = s&63;
      //   int ei = e>>6;
      //   int eo = e&63;
      //   int li =  e-1 >> 6; // last required item
      //   int lo = (e-1 & 63) + 1;
      //   long sI = a[si]; // for fixing up later
      //   long eI = a[li];
      //   System.arraycopy(g.arr, si, a, i, li-si+1);
      //   long sm = (1L<<so) - 1;
      //   a[si] = (a[si] & ~sm) | (sI&sm);
      //
      //   int fp = i*64 + o;
      //   fp+= e-s;
      //   i = fp>>6;
      //   o = fp&63;
      // }
  
      if (s==e) return;
  
      g.setEnd(false);
      if (o == 0 && (s&63) == 0) {
        int si = s>>6;
        int li = (e-1)>>6;
        System.arraycopy(g.arr, si, a, i, li-si+1);
        
        i+= (e-s)>>6;
        o = e&63;
        return;
      }
      
      
      long pM = (1L<<o) - 1; // mask of what's already in a[i]
      // System.out.println(str64(g.longFrom(s)));
      // System.out.println(str64(pM));
      BR rd = g.read();
      rd.skip(s);
      final int len = e-s;
      for (int i = 0; i < len; i++) {
        append(rd.read());
      }
      // int fp = (o<<6) + i + e-s;
      // i = fp>>6;
      // o = fp&63;
    }
  
    public BitArr finish(int[] shape) {
      return new BitArr(a, shape);
    }
  }
  
  public static String str64(long l) {
    StringBuilder t = new StringBuilder(Long.toBinaryString(l));
    while(t.length() < 64) t.insert(0, "0");
    for (int i = 56; i > 0; i-= 8)t.insert(i, '_');
    return t.toString();
  }
  
  public long longFrom(int s) {
    int i1 = s >> 6;
    int i2 = (s+63) >> 6;
    int o1 = s & 63;
    // System.out.printf("%d %d %d %d\n", s, i1, i2, o1);
    if (arr.length == i2) return arr[i1]>>>o1;
    return arr[i1]>>>o1 | arr[i2]<<(64-o1);
  }
  
  public static class BC { // boolean creator
    public long[] arr;
    int[] sz;
    private int i, o = 0; // index, offset
    public BC(int[] sz) {
      this.sz = sz;
      arr = new long[sizeof(sz)];
    }
    public void add(boolean b) {
      arr[i] |= (b? 1L : 0L)<<o;
      o++;
      // i+= o==64? 1 : 0;
      // o&= 63;
      if (o == 64) {
        o = 0;
        i++;
      }
    }
    public void add(long l) { // add a whole 64 bits; don't use together with add(bool)!
      arr[i] = l;
      i++;
    }
    public BitArr finish() {
      assert (i<<6) + o == IntStream.of(sz).reduce(1, (l, r) ->l*r);
      return new BitArr(arr, sz);
    }
  
    public void copy(BitArr arr) {
      this.arr = arr.arr;
    }
  }
  public static BC create(int[] sh) {
    return new BC(sh);
  }
  public class BR { // boolean read
    private int i, o = 0;
    public boolean read() {
      boolean r = (arr[i] & 1L<<o) != 0;
      o++;
      // i+= o==64? 1 : 0;
      // o&= 63;
      if (o == 64) {
        o = 0;
        i++;
      }
      return r;
    }
  
    public void skip(int n) {
      int fp = (i<<6) + o + n;
      i = fp>>6;
      o = fp&63;
    }
  }
  
  @Override public Value squeeze() {
    return this; // we don't need no squeezing! 
  }
  
  public BR read() {
    return new BR();
  }
}
