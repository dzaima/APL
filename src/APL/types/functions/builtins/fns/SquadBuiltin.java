package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class SquadBuiltin extends Builtin {
  @Override public String repr() {
    return "⌷";
  }
  
  public SquadBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    if (w instanceof Arr) return w;
    if (w instanceof APLMap) return ((APLMap) w).kvPair();
    throw new DomainError("⍵ not array nor map", this, w);
  }
  
  public Value call(Value a, Value w) {
    int[] p = a.asIntVec();
    int al = p.length;
    int wl = w.shape.length;
    if (al > wl) throw new RankError("⌷: expected (≢⍺) ≤ ≢⍴⍵ ("+al+" = ≢⍺; "+ Main.formatAPL(w.shape)+" ≡ ⍴⍵)", this, a);
    int sz = 1;
    for (int i = al; i < wl; i++) sz*= w.shape[i];
    int off = 0;
    for (int i = 0; i < al; i++) {
      int d = p[i]-sc.IO;
      off+= d;
      if (d<0 || d>=w.shape[i]) throw new LengthError("⌷: index out-of-bounds", this, a);
      off*= i>=al-1? sz : w.shape[i+1];
    }
    return w.cut(off, sz, Arrays.copyOfRange(w.shape, al, wl));
  }
}