package APL.types;

import APL.Main;
import APL.errors.DomainError;
import APL.types.arrs.SingleItemArr;

import java.math.BigInteger;


public class BigValue extends Primitive {
  public static final BigValue ZERO = new BigValue(BigInteger.ZERO);
  public static final BigValue ONE = new BigValue(BigInteger.ONE);
  public static final BigValue MINUS_ONE = new BigValue(BigInteger.valueOf(-1));
  public static final BigValue TWO = new BigValue(BigInteger.valueOf(2));
  
  public static final BigInteger MAX_SAFE_DOUBLE = BigInteger.valueOf(Num.MAX_SAFE_INT);
  
  public final BigInteger i;
  public BigValue(BigInteger i) {
    this.i = i;
  }
  public BigValue(double d) {
    i = bigint(d);
  }
  public BigValue(int n) {
    i = BigInteger.valueOf(n);
  }
  public BigValue(long n) {
    i = BigInteger.valueOf(n);
  }
  public static BigInteger bigint(Value w) {
    if (w instanceof Num) return bigint(((Num) w).num);
    if (w instanceof BigValue) return ((BigValue) w).i;
    throw new DomainError("Using "+w.humanType(true)+" as biginteger", w);
  }
  public static BigInteger bigint(double d) {
    if (Math.abs(d) > Num.MAX_SAFE_INT) throw new DomainError("creating biginteger from possibly rounded value");
    if (d%1 != 0) throw new DomainError("creating biginteger from non-integer");
    return BigInteger.valueOf((long) d);
  }
  
  @Override
  public Value ofShape(int[] sh) {
    return SingleItemArr.maybe(this, sh);
  }
  
  @Override public String toString() {
    if (i.signum()==-1) return "¯" + i.negate() + "L";
    return i.toString()+"L";
  }
  @Override public boolean equals(Obj o) {
    return o instanceof BigValue && i.equals(((BigValue) o).i);
  }
  
  public Num num() {
    return new Num(i.doubleValue());
  }
  @Override public int asInt() {
    int n = i.intValue();
    if (!BigInteger.valueOf(n).equals(i)) throw new DomainError("Using biginteger as integer", this);
    return n;
  }
  @Override public double asDouble() {
    if (i.abs().compareTo(MAX_SAFE_DOUBLE) > 0) throw new DomainError("Using biginteger as double", this);
    return i.doubleValue();
  }
  public int hashCode() {
    return i.hashCode();
  }
  
  public Value safePrototype() {
    return ZERO;
  }
  public static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
  public static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
  public static int safeInt(BigInteger b) {
    if (b.signum()==-1) {
      if (b.compareTo(MIN_INT)<=0) return Integer.MIN_VALUE;
    } else {
      if (b.compareTo(MAX_INT)>=0) return Integer.MAX_VALUE;
    }
    return b.intValue();
  }
  public long longValue() {
    if (i.bitLength() > 64) throw new DomainError("Using a biginteger with more than 64 bits as long", this);
    return i.longValue();
  }
}