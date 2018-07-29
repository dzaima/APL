package APL.types;

abstract public class Value extends Obj {
  public int[] shape;
  public int rank;
  public int ia; // item amount
  public Value[] arr;
  public Value prototype = Num.ZERO;
  final ArrType valtype;
  protected Value(ArrType type) {
    super(APL.Type.array);
    valtype = type;
    if (primitive()) {
      shape = new int[0];
      arr = new Value[]{this};
      ia = 1;
      rank = 0;
    }
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
}
