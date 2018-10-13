package APL.types.dimensions;

import APL.types.*;

public interface DimDFn {
  Obj call (Value a, Value w, int dim);
}