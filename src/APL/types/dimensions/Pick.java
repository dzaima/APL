package APL.types.dimensions;

import APL.Scope;
import APL.types.*;
import APL.types.functions.builtins.dops.AtBuiltin;
import APL.types.functions.builtins.fns.RShoeUBBuiltin;

public class Pick extends Settable {
  private final Variable var;
  private final Value val;
  private final Value idx;
  private final int IO;
  private final Brackets obj;
  
  public Pick(Variable var, Brackets where, Scope sc) {
    super(null);
    this.var = var;
    this.val = (Value) var.get();
    this.idx = where.val;
    this.obj = where;
    this.IO = sc.IO;
  }
  
  @Override
  public void set(Obj v, Callable blame) {
    var.update(AtBuiltin.at(v, idx, val, IO, blame));
  }
  
  public Obj get() {
    return RShoeUBBuiltin.on(idx, val, IO, obj);
  }
  
  public Obj getOrThis() {
    return get();
  }
  
  @Override
  public String toString() {
    return var.name+"["+ val +"]";
  }
}