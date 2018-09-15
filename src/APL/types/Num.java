package APL.types;

public class Num extends Value {
  public static final Num ZERO = new Num("0");
  public static final Num ONE = new Num("1");
  public static final Num TWO = new Num("2");
  public static final Num MINUS_ONE = new Num("-1");
  public static final Num E = new Num("2.71828182845904523536028747135266249775724709369995");
  private double num;
  public Num(String val) {
    super(ArrType.num);
    repr = val;
    if (val.startsWith("¯")) {
      num = -Double.parseDouble(val.substring(1));
    } else num = Double.parseDouble(val);
  }

  public Num (Num num) {
    super(ArrType.num);
    repr = num.repr;
    this.num = num.num;
  }
  public Num (int n) {
    super(ArrType.num);
    repr = Integer.toString(n);
    num = n;
  }
  public Num (long n) {
    super(ArrType.num);
    repr = Long.toString(n);
    num = n;
  }
  public Num (double val) {
    super(ArrType.num);
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
    return new Num(num % base.num);
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
