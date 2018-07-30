package APL.types;

import java.util.*;
import APL.*;

public class Arr extends Value {
  public Arr () {
    this(new ArrayList<>(), false);
  }
  public Arr (ArrayList<Value> v) {
    this(v, false);
  }
  public Arr (Value[] v) {
    this(v, false);
  }
  public Arr (Value[] v, int[] sh) {
    super(ArrType.array);
    ia = v.length;
    shape = sh;
    rank = sh.length;
    arr = v;
  }
  public Arr (ArrayList<Value> v, boolean reverse) { // 1D
    this(v.toArray(new Value[0]), reverse);
  }
  public Arr (Value[] v, boolean reverse) { // 1D
    super(ArrType.array);
    ia = v.length;
    shape = new int[]{ia};
    rank = 1;
    if (reverse) {
      arr = new Value[ia];
      for (int i = 0; i < ia; i++) {
        arr[ia-i-1] = v[i];
      }
    } else arr = v;
  }
  public Arr (int[] ps) {
    super(ArrType.array);
    rank = ps.length;
    shape = new int[rank];
    int tia = 1;
    for (int i = 0; i < ps.length; i++) {
      tia*= ps[i];
      shape[i] = ps[i];
    }
    ia = tia;
    arr = new Value[ia];
  }
  public String toString() {
    if (ia == 0) return prototype == Num.ZERO? "⍬" : "''";
    if (rank == 1 && shape[0] != 1) {
      String all = "";
      for (Value v : arr) {
        if (v.valtype == ArrType.chr) {
          char c = ((Char)v).chr;
          if (c == '\'') all+= "''";
          else all+= c;
        }
        else {
          all = null;
          break;
        }
      }
      if (all != null) return "'"+all+"'";
    }
    if (rank == 0) return "⊂"+toString(new int[0]);
    if (APL.debug&&setter) return varName+":"+toString(new int[0]);
    return toString(new int[0]);
  }
  private String toString(int[] cpos) {
    StringBuilder res = new StringBuilder(cpos.length == 0 ? "{" : "[");
    if (rank == 0) {
      return at(cpos).toString();
    } else if (cpos.length == rank-1) {
      int[] pos = new int[rank];
      System.arraycopy(cpos, 0, pos, 0, cpos.length);
      for (int i = 0; i < shape[cpos.length]; i++) {
        pos[rank-1] = i;
        if (i != 0) res.append(", ");
        res.append(at(pos));
      }
    } else {
      int[] pos = new int[cpos.length+1];
      System.arraycopy(cpos, 0, pos, 0, cpos.length);
      for (int i = 0; i < shape[cpos.length]; i++) {
        pos[cpos.length] = i;
        if (i != 0) res.append(", ");
        res.append(toString(pos));
      }
    }
    return res + (cpos.length==0?"}":"]");
  }
  private Obj at(int[] pos) {
    int x = 0;
    //printdbg(join(shape, ","), join(pos,","), rank);
    for (int i = 0; i < rank; i++) {
      x+= pos[i];
      if (i != rank-1) x*= shape[i+1];
    }
    return arr[x];
  }
}
