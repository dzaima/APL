package APL.types;

import APL.Main;
import APL.errors.DomainError;
import APL.types.arrs.SingleItemArr;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Num extends Primitive {
  
  public static final Num NEGINF = new Num(Double.NEGATIVE_INFINITY);
  public static final Num POSINF = new Num(Double.POSITIVE_INFINITY);
  public static final long MAX_SAFE_INT = 9007199254740992L;
  
  private static DecimalFormat df;
  public static int pp;
  static {
    setPrecision(14);
  }
  public static String format(double d) {
    if (d == (long)d) {
      if (d < 0) return "¯"+ (long) -d;
      else return Long.toString((long) d);
    }
    if (d < 0) return "¯"+ df.format(-d);
    else return df.format(d);
  }
  public static void setPrecision(int p) {
    pp = p;
    DecimalFormat cdf = new DecimalFormat("#.#");
    cdf.setMaximumFractionDigits(p);
    cdf.setRoundingMode(RoundingMode.HALF_UP);
    df = cdf;
  }
  public static final Num MINUS_ONE = new Num("-1");
  public static final Num ZERO  = new Num("0");
  public static final Num ONE   = new Num("1");
  public static final Num[] NUMS = new Num[256];
  static {
    for (int i = 0; i < NUMS.length; i++) {
      NUMS[i] = new Num(i);
    }
  }
  
  
  public static final Num E = new Num("2.71828182845904523536028747135266249775724709369995");
  public static final Num PI = new Num("3.1415926535897932384626433832795028841971693993751");
  public final double num;
  public Num(String val) {
    if (val.startsWith("¯")) {
      num = -Double.parseDouble(val.substring(1));
    } else num = Double.parseDouble(val);
  }
  
  public Num(int n) {
    num = n;
  }
  public Num(long n) {
    num = n;
  }
  public Num(double val) {
    num = val;
  }
  
  public static Num of(int n) {
    if (n>=0 && n<256) {
      return NUMS[n];
    }
    if (n==-1) return MINUS_ONE;
    return new Num(n);
  }
  
  public Num plus(Num w) {
    return new Num(num + w.num);
  }
  public Num divide(Num w) {
    return new Num(num / w.num);
  }
  public Num pow(Num w) {
    return new Num(Math.pow(num, w.num));
  }
  public Num minus(Num w) {
    return new Num(num - w.num);
  }
  
  public static double gcd(double... nums) {
    if (nums.length == 0) return 0;
    double res = nums[0];
    for (int i = 1; i < nums.length; i++) {
      double b = nums[i];
      while (b != 0) {
        double t = b;
        b = res % b;
        res = t;
      }
    }
    return res;
  }
  
  public static double lcm(double... nums) {
    if (nums.length == 0) return 1;
    double res = nums[0];
    for (int i = 1; i < nums.length; i++) {
      double a = nums[i];
      double b = res;
      while (b != 0) {
        double t = b;
        b = a % b;
        a = t;
      }
      if (a == 0) res = 0;
      else res = (nums[i] * res) / a;
      if (res == 0) return 0;
    }
    return res;
  }
  
  public Num conjugate() {
    return new Num(num); // no complex numbers :p
  }
  public Num negate() {
    return new Num(-num);
  }
  public Num abs() {
    if (num < 0) return new Num(-num);
    else return this;
  }
  
  public Num floor() {
    return new Num(Math.floor(num));
  }
  public Num root (Num root) {
    return new Num(Math.pow(num, 1/root.num));
  }
  public Num log (Num root) {
    return new Num(Math.log(num) / Math.log(root.num));
  }
  
  public Num binomial(Num w) {
    if (  num % 1 != 0) throw new DomainError("binomial of non-integer ⍺", this);
    if (w.num % 1 != 0) throw new DomainError("binomial of non-integer ⍵", w);
    if (w.num > num) return Num.ZERO;
  
    double res = 1;
    double a = num;
    double b = w.num;
    
    if (b > a-b) b = a-b;
    
    for (int i = 0; i < b; i++) {
      res = res * (a-i) / (i+1);
    }
    return new Num(res);
  }
  
  
  public Num ceil() {
    return new Num(Math.ceil(num));
  }

  public int compareTo(Num n) {
    return Double.compare(num, n.num);
//    if (num > n.num) return 1;
//    if (num < n.num) return -1;
//    return 0;
  }
  
  
  public boolean equals(Obj n) {
    return n instanceof Num && ((Num) n).num == num;
  }
  
  @Override
  public int asInt() { // warning: rounds
    return (int) num;
  } // TODO not round
  public double asDouble() {
    return num;
  }
  
  @Override
  public int[] asIntArr() { // TODO not round
    return new int[]{(int)num};
  }
  @Override
  public int[] asIntVec() {
    return new int[]{(int)num};
  }
  
  public String toString() {
    return format(num);
  }
  public String oneliner(int[] ignored) {
    return format(num);
  }
  
  @Override
  public Value ofShape(int[] sh) {
    if (sh.length == 0 && !Main.enclosePrimitives) return this;
    assert Arr.prod(sh) == 1;
    return new SingleItemArr(this, sh);
  }
  
  public static Num max (Num a, Num b) {
    return a.num > b.num? a : b;
  }
  public static Num min (Num a, Num b) {
    return a.num < b.num? a : b;
  }
  
  @Override
  public int hashCode() {
    if (num == 0d) return 0; // ¯0 == 0
    return Double.hashCode(num);
  }
  
  public Value safePrototype() {
    return ZERO;
  }
  
  @Override
  public Value[] valuesCopy() {
    return new Value[]{this};
  }
  
  @Override
  public double[] asDoubleArr() {
    return new double[]{num};
  }
  
  @Override
  public double[] asDoubleArrClone() {
    return new double[]{num};
  }
  
  @Override
  public boolean quickDoubleArr() {
    return true;
  }
}
