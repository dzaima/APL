package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

public class ExclBuiltin extends Builtin {
  @Override public String repr() {
    return "!";
  }
  
  
  private static double[] cache = new double[172];
  static {
    double r = 1;
    cache[0] = cache[1] = r;
    for (int i = 2; i < 172; i++) {
      r*= i;
      cache[i] = r;
    }
  }
  
  static class Nf implements NumMV {
    public Value call(Num w) {
      return w.fact();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) {
        res[i] = cache[Math.min((int) a[i], 171)];
      }
    }
  }
  private static final Nf NF = new Nf();
  
  public Obj call(Value w) {
    return numM(NF, w);
  }
  
  public Obj call(Value a0, Value w0) {
    return allD((a, w) -> ((Num) w).binomial((Num) a), a0, w0);
  }
}