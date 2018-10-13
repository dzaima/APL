package APL.types.dimensions;

import APL.Type;
import APL.types.Obj;

public class Brackets extends Obj {
  
  public int dim;
  
  public Brackets(int d) {
    
    dim = d;
  }
  
  @Override
  public Type type() {
    return Type.dim;
  }
  
  @Override
  public String toString() {
    return "["+dim+"]";
  }
}