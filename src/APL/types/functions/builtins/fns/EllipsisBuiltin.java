package APL.types.functions.builtins.fns;

import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.math.BigInteger;

public class EllipsisBuiltin extends Builtin {
  @Override public String repr() {
    return "…";
  }
  
  
  
  public Value call(Value a, Value w) {
    if (a instanceof BigValue || w instanceof BigValue) {
      BigInteger al = BigValue.bigint(a);
      BigInteger wl = BigValue.bigint(w);
      BigInteger size = al.subtract(wl).abs().add(BigInteger.ONE);
      int isize = BigValue.safeInt(size);
      if (isize==Integer.MAX_VALUE) throw new DomainError("…: expected range too large ("+a+"…"+w+")", this, w);
      
      Value[] arr = new Value[isize];
      BigInteger c = al;
      BigInteger dir = al.compareTo(wl) < 0? BigInteger.ONE : BigValue.MINUS_ONE.i;
      for (int i = 0; i < isize; i++) {
        arr[i] = new BigValue(c);
        c = c.add(dir);
      }
      return new HArr(arr);
    }
    int ai = a.asInt();
    int wi = w.asInt();
    double[] arr = new double[Math.abs(ai-wi)+1];
    if (ai>wi) {
      for (int i = 0; i < arr.length; i++) {
        arr[i] = ai - i;
      }
    } else {
      for (int i = 0; i < arr.length; i++) {
        arr[i] = ai + i;
      }
    }
    return new DoubleArr(arr);
  }
}