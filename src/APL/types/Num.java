package APL.types;

public class Num extends Value {
  public static final Num ZERO = new Num("0");
  public static final Num ONE = new Num("1");
  @SuppressWarnings("WeakerAccess")
  public static final Num MINUS_ONE = new Num("-1"); // dammit this should be public for later use
  private double num;
  public Num(String val) {
    super(ArrType.num);
    repr = val;
    num = Double.parseDouble(val);
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
  public Num times(Num w) {
    return new Num(num * w.num);
  }
  public Num minus(Num w) {
    return new Num(num - w.num);
  }
  
  public Num conjugate() {
    return new Num(num); // no complex numbers :p
  }
  public Num negate() {
    return new Num(-num);
  }

  public Num compareTo(Num n) {
    if (num > n.num) return ONE;
    if (num < n.num) return MINUS_ONE;
    return ZERO;
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
}
