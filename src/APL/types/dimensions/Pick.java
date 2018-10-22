package APL.types.dimensions;

import APL.Scope;
import APL.types.*;

import java.util.Arrays;

public class Pick extends Settable {
  
  
  private int[] pos;
  private Variable var;
  private Scope sc;
  
  public Pick(Variable v, Brackets where, Scope sc) {
    super(v.getAt(where.val.toIntArr(null), sc));
    pos = where.val.toIntArr(null);
    var = v;
    this.sc = sc;
  }
  
  @Override
  public void set(Obj v) {
    var.setAt(pos, (Value) v);
  }
  
  @Override
  public String toString() {
    return var.name+"["+ Arrays.toString(pos) +"]";
  }
}
