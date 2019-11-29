package APL.types.functions.builtins.fns;

import APL.Scope;
import APL.errors.NYIError;
import APL.types.*;
import APL.types.arrs.DoubleArr;
import APL.types.functions.Builtin;

import java.util.*;

public class RandBuiltin extends Builtin {
  @Override public String repr() {
    return "?";
  }
  
  public RandBuiltin(Scope sc) {
    super(sc);
  }
  
  class Nf implements NumMV {
    @Override public Value call(Num v) {
      if (v.equals(Num.ZERO)) return new Num(sc.rand(1d));
      else return Num.of(sc.rand(v.asInt()) + sc.IO);
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
    int IO = sc.IO;
    ArrayList<Integer> vs = new ArrayList<>(w.ia);
    int wi = w.asInt();
    int ai = a.asInt();
    for (int i = 0; i < wi; i++) {
      vs.add(i+IO);
    }
    Collections.shuffle(vs, sc.rnd);
    double[] res = new double[ai];
    for (int i = 0; i < ai; i++) {
      res[i] = vs.get(i);
    }
    return new DoubleArr(res);
  }
}