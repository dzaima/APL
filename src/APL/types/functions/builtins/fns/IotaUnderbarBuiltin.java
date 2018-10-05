package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;
import APL.types.functions.builtins.mops.ReduceBuiltin;

import java.util.ArrayList;

public class IotaUnderbarBuiltin extends Builtin {
  static Fun fn = new ReduceBuiltin().derive(new CeilingBuiltin());
  public IotaUnderbarBuiltin(Scope sc) {
    super("⍸");
    valid = 0x011;
    this.sc = sc;
  }
  
  public Obj call(Value w) {
    var sub = new ArrayList<Value>();
    for (int[] p : new Indexer(w.shape, ((Num) sc.get("⎕IO")).intValue())) {
      Num n = (Num) w.at(p, this);
      if (Main.compare(n, Num.ZERO) < 0) throw new DomainError("⍸ received negative ⍵", this, n);
      for (int i = 0, nint = n.intValue(); i < nint; i++)
      if (w.rank == 1) sub.add(new Num(p[0]));
      else sub.add(Main.toAPL(p));
    }
    return new Arr(sub);
  }
  public Obj callInv(Value w) {
    int IO = ((Num) sc.get("⎕IO")).intValue();
    int[] sh = ((Value) fn.call(w)).toIntArr(this);
    int ia = 1;
    for (int i = 0; i < sh.length; i++) {
      sh[i]+=1-IO;
      ia *= sh[i];
    }
    int[] arr = new int[ia];
    for (Value v : w.arr) {
      int[] c = v.toIntArr(this);
      arr[Indexer.fromShape(sh, c, IO)]++;
    }
    return Main.toAPL(arr, sh);
  }
  
  public Obj call(Value a, Value w) {
    return null;
  }
}