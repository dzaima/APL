package APL.types.functions;

import APL.*;
import APL.errors.LengthError;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.builtins.SetBuiltin;

import java.util.*;

public class VarArr extends Settable {
  public final ArrayList<Obj> arr;
  public final int ia;
  public VarArr(ArrayList<Obj> arr) {
    super(null);
    ia = arr.size();
    if (arr.size() > 0) this.token = arr.get(0).token;
    Collections.reverse(arr);
    this.arr = arr;
  }
  
  public Arr get() {
    if (this.token != null) Main.faulty = this;
    Value[] res = new Value[arr.size()];
    for (int i = 0; i < ia; i++) {
      Obj c = arr.get(i);
      res[i] = c instanceof VarArr? ((VarArr) c).get() : (Value) (c instanceof Value? c : ((Settable) c).get());
    }
    return Arr.create(res);
  }
  
  @Override
  public Type type() {
    return Type.array;
  }
  
  @Override public void set(Obj v) {
    set(v, false);
  }
  
  @Override
  public String toString() {
    if (Main.debug) return "vararr:"+arr;
    return get().toString();
  }
  
  public static Obj of (ArrayList<Obj> vs) {
    int sz = vs.size();
    if (sz == 0) return EmptyArr.SHAPE0Q;
    Obj fst = vs.get(0);
    if (fst instanceof Num) {
      if (((Num) fst).num == 0 || ((Num) fst).num == 1) {
        BitArr.BA bc = new BitArr.BA(sz);
        int i = sz-1;
        while (i >= 0) {
          Obj c = vs.get(i);
          if (c instanceof Num) {
            double n = ((Num) c).num;
            if (Double.doubleToRawLongBits(n)==0 || n == 1) { // don't convert negative zero!
              bc.add(n == 1);
            } else { bc = null; break; }
          } else { bc = null; break; }
          i--;
        }
        if (bc != null) return bc.finish();
      }
      double[] a = new double[sz];
      int i = 0;
      while (i < a.length) {
        Obj c = vs.get(i);
        if (c instanceof Num) {
          a[a.length-i-1] = ((Num) c).num;
          i++;
        } else {
          a = null;
          break;
        }
      }
      if (a != null) return new DoubleArr(a);
    } else if (fst instanceof Char) {
      String s = "";
      int i = sz -1;
      while (i >= 0) {
        Obj c = vs.get(i);
        if (c instanceof Char) {
          s+= ((Char) c).chr;
          i--;
        } else {
          s = null;
          break;
        }
      }
      if (s != null) return new ChrArr(s);
    }
    return new VarArr(vs);
  }
  
  public void set(Obj w, boolean update) {
    if (w instanceof Arr) {
      Arr ow = (Arr) w;
      if (ow.rank != 1) throw new LengthError("← scatter rank ≠1", ow);
      if (ow.ia != this.ia) throw new LengthError("← scatter argument lengths not equal", ow);
      for (int i = 0; i < this.ia; i++) {
        SetBuiltin.inst.callObj(this.arr.get(i), ow.get(i), update);
      }
    } else {
      for (int i = 0; i < this.ia; i++) {
        SetBuiltin.inst.callObj(this.arr.get(i), w, update);
      }
    }
  }
}