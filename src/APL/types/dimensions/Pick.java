package APL.types.dimensions;

import APL.*;
import APL.types.*;

import java.util.Arrays;

public class Pick extends Settable {
  
  
  private int[] pos;
  private Variable variable;
  private Scope sc;
  
  public Pick(Variable v, Brackets where, Scope sc) {
    super(v.getAt(where.val.asIntVec(), sc));
    pos = where.val.asIntVec();
    variable = v;
    this.sc = sc;
  }
  
  @Override
  public void set(Obj v) {
    variable.setAt(Indexer.sub(pos, sc.IO), (Value) v);
  }
  
  @Override
  public String toString() {
    return variable.name+"["+ Arrays.toString(pos) +"]";
  }
}
