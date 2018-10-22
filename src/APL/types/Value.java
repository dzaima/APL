package APL.types;

import APL.*;
import APL.errors.*;

import java.util.*;


abstract public class Value extends Obj {
  public int[] shape;
  public int rank;
  public int ia; // item amount
  public Value[] arr;
  public Value prototype = null;
  protected Value() {
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
    if (!(this instanceof Num)) throw new DomainError("Expected a number, got "+ humanType(true), caller, this);
    Num n = (Num)this;
    return n.intValue();
  }
  public boolean scalar() {
    return rank == 0;
  }
  public boolean primitive() {
    return !(this instanceof Arr);
  }
  public Value first() {
    return ia==0? prototype : arr[0];
  }
  String oneliner(int[] where) {
    throw Main.up;
  }
  
  public Value at(int[] pos, Fun f) {
    int IO = ((Num)f.sc.get("⎕IO")).toInt(f); // error here = pls take scope as arg
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
  public Value at(int[] pos, Scope sc) {
    int IO = ((Num)sc.get("⎕IO")).toInt(null);
    if (pos.length != rank) throw new RankError("array rank was "+rank+", tried to get item at rank "+pos.length, null, this);
    int x = 0;
    for (int i = 0; i < rank; i++) {
      if (pos[i] < IO) throw new DomainError("Tried to access item at position "+pos[i], null, this);
      if (pos[i] >= shape[i]+IO) throw new DomainError("Tried to access item at position "+pos[i]+" while max was "+shape[i], null, this);
      x+= pos[i]-IO;
      if (i != rank-1) x*= shape[i+1];
    }
    return arr[x];
  }
  public Value at(int[] pos, Value def) { // 0-indexed
    int x = 0;
    for (int i = 0; i < rank; i++) {
      if (pos[i] < 0 || pos[i] >= shape[i]) return def;
      x+= pos[i];
      if (i != rank-1) x*= shape[i+1];
    }
    return arr[x];
  }
  public Value simpleAt(int[] pos) {
    int x = 0;
    for (int i = 0; i < rank; i++) {
      x+= pos[i];
      if (i != rank-1) x*= shape[i+1];
    }
    return arr[x];
  }
  public int compareTo(Value v) {
    if (this instanceof Num && v instanceof Num) return ((Num) this).compareTo((Num) v);
    if (this instanceof Char && v instanceof Char) return ((Char) this).compareTo((Char) v);
    if (this instanceof Num && (   v instanceof Char ||    v instanceof Arr)) return -1;
    if (   v instanceof Num && (this instanceof Char || this instanceof Arr)) return 1;
    if ((this instanceof Arr || this instanceof Char) && (v instanceof Arr || v instanceof Char)) {
      String s1 =   fromAPL();
      String s2 = v.fromAPL();
      System.out.println(s1);
      System.out.println(s2);
      return s1.compareTo(s2);
    }
    throw new DomainError("Can't compare", v, this);
  }
  public String fromAPL() {
    throw new DomainError("can't convert to string", null, this);
  }
  public Integer[] gradeUp(Fun f) {
    if (rank != 1) throw new DomainError("grading rank ≠ 1", f, this);
    Integer[] na = new Integer[ia];
    
    for (int i = 0; i < na.length; i++) {
      na[i] = i;
    }
    Arrays.sort(na, (a, b) -> arr[a].compareTo(arr[b]));
    return na;
  }
  public Integer[] gradeDown(Fun f) {
    if (rank != 1) throw new DomainError("grading rank ≠ 1", f, this);
    Integer[] na = new Integer[ia];
    
    for (int i = 0; i < na.length; i++) {
      na[i] = i;
    }
    Arrays.sort(na, (a, b) -> arr[b].compareTo(arr[a]));
    return na;
  }
  
  public int[] eraseDim(int place) {
    int[] res = new int[rank-1];
    for (int i = 0; i < place; i++) res[i] = shape[i];
    for (int i = place; i < rank-1; i++) res[i] = shape[i+1];
    return res;
  }
  @Override
  public Type type() {
    return Type.array;
  }
  
}
