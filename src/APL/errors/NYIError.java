package APL.errors;

public class NYIError extends Error { // AKA LazyError
  public NYIError (String s) {
    super(s);
  }
}
