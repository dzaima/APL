package APL.types.dimensions;

import APL.Scope;
import APL.errors.SyntaxError;
import APL.types.*;
import APL.types.functions.Mop;

public class DervDimMop extends Mop {
  private final Mop f;
  private final int dim;
  
  public DervDimMop(Mop f, Integer dim, Scope sc) {
    super(f.repr+"["+dim+"]", sc);
    this.f = f;
    if (dim == null) this.dim = 0;
    else if (dim < 0) this.dim = dim;
    else this.dim = dim - sc.IO;
    this.token = f.token;
    
  }
  
  @Override
  public Obj call(Obj aa, Value a, Value w) {
    if (!(f instanceof DimDMop)) throw new SyntaxError("Attempt to call function dyadically that doesn't support dimension specification", a);
    return ((DimDMop) f).call(aa, a, w, dim);
  }
  
  @Override
  public Obj call(Obj aa, Value w) {
    if (!(f instanceof DimMMop)) throw new SyntaxError("Attempt to call function monadically that doesn't support dimension specification", w);
    return ((DimMMop) f).call(aa, w, dim);
  }
}
