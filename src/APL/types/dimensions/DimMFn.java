package APL.types.dimensions;

import APL.types.Value;

public interface DimMFn {
  Value call (Value w, int dim);
}