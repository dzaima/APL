package APL.types.dimensions;

import APL.types.Value;

public interface DimDFn {
  Value call (Value a, Value w, DervDimFn dim);
}