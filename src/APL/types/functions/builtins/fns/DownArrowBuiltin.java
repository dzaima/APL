package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.dimensions.*;
import APL.types.functions.Builtin;

import java.util.Arrays;

public class DownArrowBuiltin extends Builtin implements DimDFn {
  @Override public String repr() {
    return "↓";
  }
  
  
  public Value call(Value w) {
    if (w instanceof Primitive) return w;
    if (w.rank <= 1) return new Rank0Arr(w);
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
    int[] gsh = a.asIntVec();
    if (gsh.length == 0) return w;
    if (gsh.length > w.rank) throw new DomainError("↓: ≢⍺ should be less than ⍴⍴⍵ ("+gsh.length+" = ≢⍺; "+Main.formatAPL(w.shape)+" ≡ ⍴⍵)", this);
    int[] sh = new int[w.rank];
    System.arraycopy(gsh, 0, sh, 0, gsh.length);
    System.arraycopy(w.shape, gsh.length, sh, gsh.length, sh.length - gsh.length);
    int[] off = new int[sh.length];
    for (int i = 0; i < gsh.length; i++) {
      int am = gsh[i];
      sh[i] = w.shape[i] - Math.abs(am);
      if (am > 0) off[i] = am;
    }
    return UpArrowBuiltin.on(sh, off, w, this);
  }
  
  public Value call(Value a, Value w, DervDimFn dims) {
    int[] axV = a.asIntVec();
    int[] axK = dims.dims(w.rank);
    if (axV.length != axK.length) throw new DomainError("↓: expected ⍺ and axis specification to have equal number of items (⍺≡"+Main.formatAPL(axV)+"; axis≡"+dims.format()+")", dims);
    int[] sh = w.shape.clone();
    int[] off = new int[sh.length];
    for (int i = 0; i < axV.length; i++) {
      int ax = axK[i];
      int am = axV[i];
      sh[ax] = w.shape[ax] - Math.abs(am);
      if (am > 0) off[ax] = am;
    }
    return UpArrowBuiltin.on(sh, off, w, this);
  }
  
  public Value underW(Obj o, Value a, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(a, w)) : (Value) o;
    int[] ls = a.asIntVec();
    int[] sh = w.shape;
    for (int i = 0; i < ls.length; i++) {
      ls[i] = ls[i]>0? ls[i]-sh[i] : ls[i]+sh[i];
    }
    return UpArrowBuiltin.undo(ls, v, w, this);
  }
  
  public Value under(Obj o, Value w) {
    Value v = o instanceof Fun? ((Fun) o).call(call(w)) : (Value) o;
    Value[] vs = v.values();
    if (vs.length > 0) {
      int[] sh = vs[0].shape;
      for (int i = 1; i < vs.length; i++) {
        if (!Arrays.equals(vs[i].shape, sh)) throw new LengthError("⍢↓: undoing expected arrays of equal shapes ("+Main.formatAPL(sh)+" ≢ "+Main.formatAPL(vs[i].shape)+")", this, o);
      }
    }
    return UpArrowBuiltin.merge(vs, v.shape, this);
  }
}