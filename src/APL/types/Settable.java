package APL.types;

import APL.*;
import APL.errors.*;

public abstract class Settable extends Obj {
  final Obj v;
  protected Settable(Obj v) {
    this.v = v;
  }
  @Override
  public Type type() {
    return v == null? Type.var : v.type();
  }
  public abstract void set(Obj v);
  public Obj get() {
    if (v == null) throw new ValueError("tying to get value of non-existing settable");
    return v;
  }
}
