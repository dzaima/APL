package APL;

public enum Type {
  array('N'), var('V'), nul('V'), gettable('#'),
  fn('F'),  mop('M'),  dop('D'),
  set('←'), dim('@');
  
  public final char chr;
  
  Type(char chr) {
    this.chr = chr;
  }
}