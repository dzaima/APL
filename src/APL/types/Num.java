package APL.types;

import APL.Main;
import APL.errors.DomainError;
import APL.types.arrs.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Num extends Primitive {
  
  static DecimalFormat df;
  public static int pp;
  static {
    setPrecision(14);
  }
  public static void setPrecision(int p) {
    pp = p;
    DecimalFormat cdf = new DecimalFormat("#.#");
    cdf.setMaximumFractionDigits(p);
    cdf.setRoundingMode(RoundingMode.HALF_UP);
    df = cdf;
  }
  public static final Num ZERO = new Num("0");
  public static final Num ONE = new Num("1");
  public static final Num TWO = new Num("2");
  public static final Num MINUS_ONE = new Num("-1");
  public static final Num E = new Num("2.71828182845904523536028747135266249775724709369995");
  public static final Num PI = new Num("3.1415926535897932384626433832795028841971693993751");
  public static final Num I1 = null; // no imaginary numbers :'(
  @SuppressWarnings("WeakerAccess") // no, bad
  public static final Num INFINITY = new Num("1e309");
  public final double num;
  public Num(String val) {
    repr = val;
    if (val.startsWith("¯")) {
      num = -Double.parseDouble(val.substring(1));
    } else num = Double.parseDouble(val);
  }

  public Num(int n) {
    repr = Integer.toString(n);
    num = n;
  }
  public Num(long n) {
    repr = Long.toString(n);
    num = n;
  }
  public Num(double val) {
    repr = Double.toString(val);
    num = val;
  }

  public Num plus(Num w) {
    return new Num(num + w.num);
  }
  public Num divide(Num w) {
    return new Num(num / w.num);
  }
  public Num floorDivide(Num w) {
    return new Num(Math.floor(num / w.num));
  }
  public Num times(Num w) {
    return new Num(num * w.num);
  }
  public Num pow(Num w) {
    return new Num(Math.pow(num, w.num));
  }
  public Num minus(Num w) {
    return new Num(num - w.num);
  }
  public Num mod(Num base) {
    double d = num % base.num;
    if (d < 0) return new Num(d+base.num);
    return new Num(d);
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
  
  public Num fact() {
    if (num > 170) return Num.INFINITY;
    if (num % 1 != 0) throw new DomainError("factorial of non-integer", this);
    if (num < 0) throw new DomainError("factorial of negative number", this);
    double res = IntStream.range(2, (int) (num+1)).asDoubleStream().reduce(1, (a, b) -> a * b);
    return new Num(res);
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
  
  public Num sin() { return new Num(Math.sin(num)); }
  public Num cos() { return new Num(Math.cos(num)); }
  public Num tan() { return new Num(Math.tan(num)); }
  
  public Num asin() { return new Num(Math.asin(num)); }
  public Num acos() { return new Num(Math.acos(num)); }
  public Num atan() { return new Num(Math.atan(num)); }
  
  public Num sinh() { return new Num(Math.sinh(num)); }
  public Num cosh() { return new Num(Math.cosh(num)); }
  public Num tanh() { return new Num(Math.tanh(num)); }
  
  public Num asinh() { throw new DomainError("inverse hyperbolic functions NYI"); }
  public Num acosh() { throw new DomainError("inverse hyperbolic functions NYI"); }
  public Num atanh() { throw new DomainError("inverse hyperbolic functions NYI"); }
  
  public Num real() { return this; }
  @SuppressWarnings("SameReturnValue") // no imaginary numbers.. for now
  public Num imag() { return Num.ZERO; }
  
  
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
  public int[] asIntVec() { // TODO not round
    return new int[]{(int)num};
  }
  
  public String toString() {
    if (num == (int)num) return Integer.toString((int)num);
  
    return df.format(num);
  }
  public String oneliner(int[] ignored) {
    if (num == (int)num) return Integer.toString((int)num);
    return String.format("%.5g%n", 0.912385);
  }
  
  @Override
  public Value ofShape(int[] sh) {
    if (sh.length == 0 && !Main.enclosePrimitives) return this;
    assert Arrays.stream(sh).reduce(1, (a, b) -> a*b) == 1;
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
    return Double.hashCode(num);
  }
  
  @Override
  public Value prototype() {
    return ZERO;
  }
  
  @Override
  public Value[] values() {
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
