package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.NYIError;
import APL.types.*;
import APL.types.functions.Builtin;

public class RandBuiltin extends Builtin {
  public RandBuiltin(Scope sc) {
    super("?", 0x011, sc);
  }
  
  class Nf implements NumMV {
    @Override public Value call(Num v) {
      if (v.equals(Num.ZERO)) return new Num(sc.rand(1d));
      else return new Num(sc.rand(v.asInt()) + sc.IO);
    }
  
    @Override public void call(double[] res, double[] a) {
      for (int i = 0; i < res.length; i++) {
        res[i] = a[i]==0? sc.rand(1d) : sc.rand((int) a[i]) + sc.IO;
      }
    }
  }
  private final Nf nf = new Nf();
  
  public Obj call(Value w) {
    return numM(nf, w);
  }
  
  public Obj call(Value a, Value w) {
    throw new NYIError("sorry", a);
  }
}