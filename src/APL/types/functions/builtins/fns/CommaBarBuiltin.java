package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class CommaBarBuiltin extends Builtin {
  public CommaBarBuiltin() {
    super("⍪", 0x011);
  }
  
  public Obj call(Value w) {
    if (w.rank==1 && w.shape[0]==0) return new EmptyArr(new int[]{0, 1});
    if (w.rank==0) return new HArr(w.values(), new int[]{1, 1});
    int[] nsh = new int[]{w.shape[0], w.ia/w.shape[0]};
    return w.ofShape(nsh);
  }
  
  public Obj call(Value a, Value w) {
    if (a.rank != w.rank) throw new RankError("Ranks for ⍪ must be equal", w);
    if (a.ia / a.shape[0] != w.ia / w.shape[0]) throw new LengthError("All but the 1st dimension must be equal for ⍪", w);
    if (a.rank == 0) return new HArr(new Value[]{a, w});
    int[] nsh = new int[a.rank];
    System.arraycopy(a.shape, 1, nsh, 1, a.rank - 1);
    nsh[0] = a.shape[0] + w.shape[0];
    
    if (a.quickDoubleArr() && w.quickDoubleArr()) {
      double[] res = new double[a.ia + w.ia];
      double[] av = a.asDoubleArr();
      double[] wv = w.asDoubleArr();
      System.arraycopy(av, 0, res, 0, a.ia);
      System.arraycopy(wv, 0, res, a.ia, w.ia);
  
      return new DoubleArr(res, nsh);
    }
    Value[] res = new Value[a.ia + w.ia];
    Value[] av = a.values();
    Value[] wv = w.values();
    System.arraycopy(av, 0, res, 0, a.ia);
    System.arraycopy(wv, 0, res, a.ia, w.ia);
    
    return new HArr(res, nsh);
  }
}