package APL.types;

import APL.errors.DomainError;

public class Num extends Value {
  public static final Num ZERO = new Num("0");
  public static final Num ONE = new Num("1");
  public static final Num TWO = new Num("2");
  public static final Num MINUS_ONE = new Num("-1");
  public static final Num E = new Num("2.71828182845904523536028747135266249775724709369995");
  public static final Num PI = new Num("3.1415926535897932384626433832795028841971693993751");
  private double num;
  public static final Num I1 = null; // no imaginary numbers :'(
  public Num(String val) {
    super(ArrType.num);
    repr = val;
    if (val.startsWith("¯")) {
      num = -Double.parseDouble(val.substring(1));
    } else num = Double.parseDouble(val);
    prototype = Num.ZERO;
  }

  public Num (Num num) {
    super(ArrType.num);
    repr = num.repr;
    this.num = num.num;
    prototype = Num.ZERO;
  }
  public Num (int n) {
    super(ArrType.num);
    repr = Integer.toString(n);
    num = n;
    prototype = Num.ZERO;
  }
  public Num (long n) {
    super(ArrType.num);
    repr = Long.toString(n);
    num = n;
    prototype = Num.ZERO;
  }
  public Num (double val) {
    super(ArrType.num);
    repr = Double.toString(val);
    num = val;
    prototype = Num.ZERO;
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
  
  public static Num gcd(Num[] nums) {
    double res = nums[0].num;
    for (int i = 1; i < nums.length; i++) {
      double b = nums[i].num;
      while (b != 0) {
        double t = b;
        b = res % b;
        res = t;
      }
    }
    return new Num(res);
  }
  
  public static Num lcm(Num[] nums) {
    double res = nums[0].num;
    for (int i = 1; i < nums.length; i++) {
      double a = nums[i].num;
      double b = res;
      while (b != 0) {
        double t = b;
        b = a % b;
        a = t;
      }
      
      res = (nums[i].num * res) / a;
    }
    return new Num(res);
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
    if (n instanceof Num) {
      return ((Num)n).num == num;
    }
    return false;
  }
  
  public int intValue() {
    return (int)num;
  }
  public double doubleValue() {
    return num;
  }

  public String toString() {
    if (num == (int)num) return Integer.toString((int)num);
    return Double.toString(num);
  }
  protected String oneliner(int[] ignored) {
    if (num == (int)num) return Integer.toString((int)num);
    return Double.toString(num);
  }
  
  public static Num max (Num a, Num b) {
    return a.num > b.num? a : b;
  }
  public static Num min (Num a, Num b) {
    return a.num < b.num? a : b;
  }
  
}
