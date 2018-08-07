package APL.types;

import APL.Main;
import APL.Type;
import APL.errors.DomainError;
import APL.errors.RankError;


abstract public class Value extends Obj {
  public int[] shape;
  public int rank;
  public int ia; // item amount
  public Value[] arr;
  public Value prototype = Num.ZERO;
  public final ArrType valtype;
  protected Value(ArrType type) {
    super(Type.array);
    valtype = type;
    if (primitive()) {
      shape = new int[0];
      arr = new Value[]{this};
      ia = 1;
      rank = 0;
    }
  }
  public int[] toIntArr(Fun caller) {
    if (rank > 1) throw new RankError("Expected rank <= 1, got " + rank, caller, this);
    int[] res = new int[ia];
    for (int i = 0; i < arr.length; i++) {
      res[i] = arr[i].toInt(caller);
    }
    return res;
  }
  public int toInt(Fun caller) {
    if (valtype != ArrType.num) throw new DomainError("Expected a number, got "+ Main.human(valtype, true), caller, this);
    Num n = (Num)this;
    return n.intValue();
  }
  public boolean scalar() {
    return rank == 0;
  }
  public boolean primitive() {
    return valtype !=ArrType.array;
  }
  public Value first() {
    return ia==0? prototype : arr[0];
  }
  protected String oneliner(int[] where) {
    throw Main.up;
  }
  
  public Value at(int[] pos, Fun f) {
    int IO = ((Num)f.sc.get("⎕IO")).toInt(f);
    if (pos.length != rank) throw new RankError("array rank was "+rank+", tried to get item at rank "+pos.length, f, this);
    int x = 0;
    for (int i = 0; i < rank; i++) {
      if (pos[i] < IO) throw new DomainError("Tried to access item at position "+pos[i], f, this);
      if (pos[i] >= shape[i]+IO) throw new DomainError("Tried to access item at position "+pos[i]+" while max was "+shape[i], f, this);
      x+= pos[i]-IO;
      if (i != rank-1) x*= shape[i+1];
    }
    return arr[x];
  }
}
