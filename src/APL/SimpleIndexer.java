package APL;

import java.util.Iterator;

public class SimpleIndexer implements Iterable<Integer> { // todo this is horrible (or at least i think so, i can't be bothered to understand it)
  private final int[] shape;
  private final int[] chosen;
  private final int[] shapeTP;
  private final int len;
  
  public SimpleIndexer(int[] shape, int[] chosen) {
    this.shape = shape;
    this.chosen = chosen;
    len = shape.length;
    this.shapeTP = new int[len+1];
    int p = 1;
    shapeTP[len] = 1;
    for (int i = len-1; i >= 0; i--) {
      p*= shape[i];
      shapeTP[i] = p;
    }
  }
  
  @Override public Iterator<Integer> iterator() {
    boolean empty = true;
    for (int i : chosen) {
      if (i != 0) { empty = false; break; }
    }
    boolean finalEmpty = empty & chosen.length!=0;
    //noinspection Convert2Diamond java 8
    return new Iterator<Integer>() {
      int index = 0;
      boolean hasNext = !finalEmpty;
      @Override public boolean hasNext() {
        return hasNext;
      }
      
      @Override public Integer next() {
        int ret = index;
        index++;
        int d = len-1;
        while (d!=-1) {
          if (index%shapeTP[d] == (chosen[d]*shapeTP[d+1])%shapeTP[d]) {
            index+= (shape[d] - chosen[d])*shapeTP[d+1];
            d--;
          }
          else break;
        }
        if (d==-1) hasNext =false;
        return ret;
      }
    };
  }
}