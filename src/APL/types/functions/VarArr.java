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
    if (vs.size() == 0) return EmptyArr.SHAPE0;
    if (vs.get(0) instanceof Num) {
      double[] a = new double[vs.size()];
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
    } else {
      String s = "";
      int i = vs.size()-1;
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
        SetBuiltin.inst.call(this.arr.get(i), ow.get(i), update);
      }
    } else {
      for (int i = 0; i < this.ia; i++) {
        SetBuiltin.inst.call(this.arr.get(i), w, update);
      }
    }
  }
}
