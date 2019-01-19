package APL.types.dimensions;

import APL.Scope;
import APL.errors.SyntaxError;
import APL.types.*;

public class DervDimFn extends Fun {
  private final Fun f;
  private final int dim;
  
  @Override public String repr() {
    return f.repr()+"["+dim+"]";
  }
  
  public DervDimFn(Fun f, Integer dim, Scope sc) {
    super(sc);
    this.f = f;
    if (dim == null) this.dim = 0;
    else if (dim < 0) this.dim = dim;
    else this.dim = dim - sc.IO;
    this.token = f.token;
    
  }
  
  @Override
  public Obj call(Value a, Value w) {
    if (!(f instanceof DimDFn)) throw new SyntaxError("Attempt to call function dyadically that doesn't support dimension specification", a);
    return ((DimDFn) f).call(a, w, dim);
  }
  
  @Override
  public Obj call(Value w) {
    if (!(f instanceof DimMFn)) throw new SyntaxError("Attempt to call function monadically that doesn't support dimension specification", w);
    return ((DimMFn) f).call(w, dim);
  }
}
