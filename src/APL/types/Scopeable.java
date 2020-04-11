package APL.types;

import APL.Scope;

public abstract class Scopeable extends Obj {
  final public Scope sc;
  protected Scopeable(Scope sc) {
    this.sc = sc;
  }
}