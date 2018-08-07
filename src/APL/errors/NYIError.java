package APL.errors;

public class NYIError extends APLError { // AKA LazyError
  public NYIError (String s) {
    super(s);
  }
}
