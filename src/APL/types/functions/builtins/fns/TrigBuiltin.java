package APL.types.functions.builtins.fns;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

public class TrigBuiltin extends Builtin {
  public TrigBuiltin() {
    super("○");
    valid = 0x011;
  }
  
  public Obj call(Value w) {
    return scalar(v -> Num.PI.times((Num) v), w);
  }
  public Obj call(Value a0, Value w0) {
    return scalar((a, w) -> {
      Num n = (Num) w;
      switch(a.toInt(this)) {
        case  1: return n.sin();
        case  2: return n.cos();
        case  3: return n.tan();
        case  4: return n.pow(Num.TWO).plus(Num.ONE).root(Num.TWO);
        case  5: return n.sinh();
        case  6: return n.cosh();
        case  7: return n.tanh();
        case  8: return n.pow(Num.TWO).plus(Num.ONE).negate().root(Num.TWO);
        case  9: return n.real();
        case 10: return n.abs();
        case 11: return n.imag();
        case 12: throw new DomainError("what is phase", this, a);
        
        case  0: return Num.ONE.minus(n.pow(Num.TWO)).root(Num.TWO);
        
        case  -1: return n.asin();
        case  -2: return n.acos();
        case  -3: return n.atan();
        case  -4: return n.pow(Num.TWO).minus(Num.ONE).root(Num.TWO);
        case  -5: return n.asinh();
        case  -6: return n.acosh();
        case  -7: return n.atanh();
        case  -8: return n.pow(Num.TWO).plus(Num.ONE).negate().root(Num.TWO).negate();
        case  -9: return n;
        case -10: return n.conjugate();
        case -11: return n.times(Num.I1);
        case -12: return Num.E.pow(n.times(Num.I1));
      }
      throw new DomainError("⍺ of ○ out of bounds", this, a);
    }, a0, w0);
  }
}