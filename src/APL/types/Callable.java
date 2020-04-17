package APL.types;

import APL.Scope;

public abstract class Callable extends Obj {
  final public Scope sc;
  protected Callable(Scope sc) {
    this.sc = sc;
  }
}