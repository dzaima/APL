package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class ExclBuiltin extends Builtin {
  public ExclBuiltin() {
    super("!", 0x011);
  }
  static double[] cache = new double[172];
  static {
    double r = 1;
    cache[1] = r;
    for (int i = 2; i < 172; i++) {
      r*= i;
      cache[i] = r;
    }
    System.out.println(Arrays.toString(cache));
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
    return allM((a, w) -> ((Num) w).binomial((Num) a), a0, w0);
  }
}