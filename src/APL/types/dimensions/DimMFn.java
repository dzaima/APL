package APL.types.dimensions;

import APL.types.*;

public interface DimMFn {
  Value call (Value w, int dim);
}