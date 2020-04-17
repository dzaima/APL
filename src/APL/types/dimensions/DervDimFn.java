package APL.types.dimensions;

import APL.*;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.builtins.fns.CatBuiltin;

public class DervDimFn extends Fun {
  private final int IO;
  private final Fun f;
  private final int[] raw;
  private final int[] dims;
  
  @Override public String repr() {
    return f.repr()+"["+ Main.formatAPL(raw)+"]";
  }
  
  public DervDimFn(Fun f, int[] raw, Scope sc) {
    super(sc);
    this.f = f;
    this.raw = raw;
    this.IO = sc.IO;
    if (raw == null) dims = new int[]{0};
    else {
      dims = new int[raw.length];
      for (int i = 0; i < raw.length; i++) {
        int c = raw[i];
        if (c==0 && IO==1) throw new DomainError("bracket axis cannot contain 0 with ⎕IO←1");
        dims[i] = c<0? c : c-IO;
      }
    }
    this.token = f.token;
  }
  
  public int[] dims(int rank) {
    int[] res = new int[dims.length];
    boolean[] used = new boolean[rank];
  
    for (int i = 0; i < dims.length; i++) {
      int c = dims[i];
      int ax = c < 0? c + rank : c;
      if (used[ax]) throw new DomainError("function axis specified axis "+(ax+IO)+" twice", this);
      else used[ax] = true;
      res[i] = ax;
    }
    return res;
  }
  
  @Override
  public Value call(Value a, Value w) {
    if (!(f instanceof DimDFn)) throw new SyntaxError("Attempt to dyadically call "+this+", which doesn't support dimension specification", this);
    return ((DimDFn) f).call(a, w, this);
  }
  
  @Override
  public Value call(Value w) {
    if (!(f instanceof DimMFn)) throw new SyntaxError("Attempt to monadically call "+this+", which doesn't support dimension specification", this);
    if (dims.length != 1) throw new DomainError(repr()+" expected one dimension specifier");
    return ((DimMFn) f).call(w, dims[0]);
  }
  
  public int singleDim(int rank) {
    if (dims.length != 1) throw new DomainError(f+" expected only one axis specifier", f);
    int c = dims[0];
    if (c < 0) c+= rank;
    return c;
  }
  public int singleDim() {
    if (dims.length != 1) throw new DomainError(f+" expected only one axis specifier", f);
    return dims[0];
  }
  
  public String format() {
    return Main.formatAPL(raw);
  }
}