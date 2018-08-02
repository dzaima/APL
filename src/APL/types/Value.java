package APL.types;

import APL.APL;
import APL.Type;
import APL.errors.DomainError;
import APL.errors.RankError;


abstract public class Value extends Obj {
  public int[] shape;
  public int rank;
  public int ia; // item amount
  public Value[] arr;
  public Value prototype = Num.ZERO;
  final ArrType valtype;
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
  public int[] toIntArr() {
    if (rank > 1) throw new RankError("Expected rank <= 1, got " + rank);
    int[] res = new int[ia];
    for (int i = 0; i < arr.length; i++) {
      res[i] = arr[i].toInt();
    }
    return res;
  }
  public int toInt() {
    if (valtype != ArrType.num) throw new DomainError("Expected number, got "+APL.human(valtype));
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
    throw APL.up;
  }
}
