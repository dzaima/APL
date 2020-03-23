package APL.types.dimensions;

import APL.types.*;

public interface DimDFn {
  Value call (Value a, Value w, int dim);
}