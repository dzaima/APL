package APL.types.functions.builtins.fns;

import APL.types.*;
import APL.types.functions.Builtin;

import java.util.stream.IntStream;

public class OrBuiltin extends Builtin {
  public OrBuiltin() {
    super("∨");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return Num.gcd(IntStream.range(0, w.ia).mapToObj(i -> (Num) w.arr[i]).toArray(Num[]::new));
  }
  
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> Num.gcd(new Num[]{(Num) a, (Num) w}), a0, w0);
  }
}