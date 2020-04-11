package APL.types.functions.builtins.fns;

import APL.*;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

public class DownArrowBuiltin extends Builtin {
  @Override public String repr() {
    return "↓";
  }
  
  public DownArrowBuiltin(Scope sc) {
    super(sc);
  }
  
  public Value call(Value w) {
    if (w instanceof Primitive) return w;
    if (w.rank <= 1) return new Rank0Arr(w);
    // TODO stupid dimensions
    if (w.quickDoubleArr()) {
      double[] dw = w.asDoubleArr();
      int csz = w.shape[w.rank-1]; // chunk size
      int cam = w.ia/csz; // chunk amount
      Value[] res = new Value[cam];
      for (int i = 0; i < cam; i++) {
        double[] c = new double[csz];
        System.arraycopy(dw, i*csz, c, 0, csz);
        // ↑ ≡ for (int j = 0; j < csz; j++) c[j] = dw[i * csz + j];
        res[i] = new DoubleArr(c);
      }
      int[] nsh = new int[w.rank-1];
      System.arraycopy(w.shape, 0, nsh, 0, nsh.length);
      return new HArr(res, nsh);
    }
    int csz = w.shape[w.rank-1]; // chunk size
    int cam = w.ia/csz; // chunk amount
    Value[] res = new Value[cam];
    for (int i = 0; i < cam; i++) {
      Value[] c = new Value[csz];
      for (int j = 0; j < csz; j++) {
        c[j] = w.get(i*csz + j);
      }
      res[i] = Arr.create(c);
    }
    int[] nsh = new int[w.rank-1];
    System.arraycopy(w.shape, 0, nsh, 0, nsh.length);
    return new HArr(res, nsh);
  }
  
  public Value call(Value a, Value w) {
    return on(a, w, sc.IO);
  }
  public static Value on(Value a, Value w, int IO) { // TODO ⍴⍺ < ⍴⍴⍵; a - amount; w - array
    // TODO redo this, merge with UpArrowBuiltin
    if (w instanceof BitArr && w.rank == 1) {
      BitArr wb = (BitArr) w;
      int n;
      if (a instanceof Num) n = a.asInt();
      else {
        int[] ns = a.asIntVec();
        if(ns.length != 1) throw new DomainError("↓ expected (≢⍺) ≡ ≢⍴⍵");
        n = ns[0];
      }
      if (n < 0) {
        int am = w.ia+n;
        if (am < 0) throw new DomainError("↓ expected (|⍺) ≤ ⍴⍵");
        long[] ls = new long[BitArr.sizeof(am)];
        System.arraycopy(wb.arr, 0, ls, 0, ls.length);
        return new BitArr(ls, new int[]{am});
      } else {
        int am = w.ia - n;
        if (am < 0) throw new DomainError("↓ expected (|⍺) ≤ ⍴⍵");
        BitArr.BA res = new BitArr.BA(am);
        res.add(wb, n, w.ia);
        return res.finish();
      }
    }
    
    int[] shape = a.asIntVec();
    if (shape.length == 0) return w;
    int ia = 1;
    int[] offsets = new int[shape.length];
    for (int i = 0; i < shape.length; i++) {
      int d = shape[i];
      if (d < 0) {
        d = -d;
        offsets[i] = IO;
      } else {
        offsets[i] = d + IO;
      }
      shape[i] = a.shape[i] - d;
      ia *= shape[i];
    }
    Value[] arr = new Value[ia];
    Indexer indexer = new Indexer(shape, offsets);
    int i = 0;
    for (int[] index : indexer) {
      arr[i] = w.at(index, IO).squeeze();
      i++;
    }
    return Arr.create(arr, shape);
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(a, w)) : (Value) o;
    int[] ls = a.asIntVec();
    int[] sh = w.shape;
    for (int i = 0; i < ls.length; i++) {
      ls[i] = ls[i]>0? ls[i]-sh[i] : ls[i]+sh[i];
    }
    return UpArrowBuiltin.undo(ls, v, w);
  }
}