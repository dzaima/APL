package APL.types;

import APL.*;
import APL.errors.DomainError;
import APL.types.arrs.*;

import java.util.Arrays;
import java.util.stream.*;

public abstract class Arr extends Value {
  public Arr(int[] shape) {
    super(shape);
  }
  public Arr(int[] shape, int ia) {
    super(shape, ia, shape.length);
  }
  public Arr(int[] shape, int ia, int rank) {
    super(shape, ia, rank);
  }
  
  public String string(boolean quote) {
    if (rank == 1/* && shape[0] != 1*/) { // strings
      StringBuilder all = new StringBuilder();
      for (Value v : this) {
        if (v instanceof Char) {
          char c = ((Char) v).chr;
          if (quote && c == '\"') all.append("\"\"");
          else all.append(c);
        } else return null;
      }
      if (quote)
        return "\"" + all + "\"";
      else return all.toString();
    }
    return null;
  }
  public String toString() {
    if (ia == 0) {
      if (rank == 1) return prototype() == Num.ZERO? "⍬" : prototype() instanceof Char? "''" : "⍬";
      else {
        String s = IntStream.range(0, rank).mapToObj(i -> String.valueOf(shape[i])).collect(Collectors.joining(" "));
        return s + "⍴" + (prototype() == Num.ZERO? "⍬" : prototype() instanceof Char? "''" : "⍬");
      }
    }
    String qs = string(Main.quotestrings || Main.noBoxing);
    if (qs != null) return qs;
    if (Main.noBoxing) {
      if (rank == 0) return "⊂" + oneliner();
      return oneliner();
    } else {
      if (rank == 0) return "⊂"+first().toString();
      if (rank == 1) { // simple vectors
        StringBuilder res = new StringBuilder();
        var simple = true;
        for (Value v : this) {
          if (res.length() > 0) res.append(" ");
          if (v == null) {
            res.append("JAVANULL");
          } else {
            simple &= v instanceof Primitive;
            res.append(v.toString());
          }
        }
        if (simple) return res.toString();
      }
      
      if (rank == 2) {
        boolean charmat = true;
        if (!(this instanceof ChrArr)) {
          for (Value v : this) {
            if (!(v instanceof Char)) {
              charmat = false;
            }
          }
        }
        
        if (charmat) {
          StringBuilder b = new StringBuilder();
          int i = 0;
          for (Value v : this) {
            if (i++ % shape[1] == 0 && i!=1) b.append('\n');
            b.append(((Char) v).chr);
          }
          return b.toString();
        }
      }
      
      if (rank < 3) { // boxed arrays
        int w = rank==1? shape[0] : shape[1];
        int h = rank==1? 1 : shape[0];
        String[][][] stringified = new String[w][h][];
        int[][] itemWidths = new int[w][h];
        int[] widths = new int[w];
        int[] heights = new int[h];
        var simple = true;
        int x=0, y=0;
        for (Value v : this) {
          if (v == null) v = Main.toAPL("JAVANULL");
          simple &= v instanceof Primitive;
          var c = v.toString().split("\n");
          var cw = 0;
          for (var ln : c) {
            cw = Math.max(ln.length(), cw);
          }
          itemWidths[x][y] = cw;
          widths[x] = Math.max(widths[x], cw);
          heights[y] = Math.max(heights[y], c.length);
          stringified[x][y] = c;
          x++;
          if (x==w) {
            x = 0;
            y++;
          }
        }
        int borderSize = simple? 0 : 1;
        int rw = simple? -1 : 1,
          rh = borderSize ; // result w&h;
        for (x = 0; x < w; x++) rw+= widths[x]+1;
        for (y = 0; y < h; y++) rh+= heights[y]+borderSize;
        char[][] chars = new char[rh][rw];
        int rx = borderSize , ry; // x&y in chars
        for (x = 0; x < w; x++) {
          ry = borderSize;
          for (y = 0; y < h; y++) {
            String[] cobj = stringified[x][y];
            for (int cy = 0; cy < cobj.length; cy++) {
              String s = cobj[cy];
              char[] line = s.toCharArray();
              int sx = get(y*w + x) instanceof Num? rx+widths[x]-itemWidths[x][y] : rx;
              System.arraycopy(line, 0, chars[ry + cy], sx, line.length);
            }
            ry+= heights[y]+borderSize;
          }
          rx+= widths[x]+1;
        }
        if (!simple) { // draw borders
          rx = 0;
          for (x = 0; x < w; x++) {
            ry = 0;
            for (y = 0; y < h; y++) {
              chars[ry][rx] = '┼';
              for (int cx = 1; cx <=  widths[x]; cx++) chars[ry][rx+cx] = '─';
              for (int cy = 1; cy <= heights[y]; cy++) chars[ry+cy][rx] = '│';
              if (x == 0) {
                for (int cy = 1; cy <= heights[y]; cy++) chars[ry+cy][rw-1] = '│';
                chars[ry][rw-1] = y==0? '┐' : '┤';
                chars[ry][0] = '├';
              }
              ry+= heights[y]+borderSize;
            }
            chars[0][rx] = '┬';
            chars[rh-1][rx] = x==0?'└' : '┴';
            for (int cx = 1; cx <=  widths[x]; cx++) chars[rh-1][rx+cx] = '─';
            rx+= widths[x]+1;
          }
          chars[0][0] = '┌';
          chars[rh-1][rw-1] = '┘';
        }
        for (char[] ca : chars) {
          for (int i = 0; i < ca.length; i++) {
            if (ca[i] == 0) ca[i] = ' ';
          }
        }
        StringBuilder res = new StringBuilder();
        boolean next = false;
        for (char[] ln : chars) {
          if (next) res.append('\n');
          res.append(ln);
          next = true;
        }
        return res.toString();
      } else return oneliner();
    }
  }
  public String oneliner(int[] where) {
    var qs = string(true);
    if (qs != null) return qs;
    StringBuilder res = new StringBuilder(where.length == 0 ? "{" : "[");
    if (rank == 0) {
      return first().oneliner();
    } else if (where.length == rank-1) {
      int[] pos = new int[rank];
      System.arraycopy(where, 0, pos, 0, where.length);
      for (int i = 0; i < shape[where.length]; i++) {
        pos[rank-1] = i;
        if (i != 0) res.append(", ");
        res.append(simpleAt(pos).oneliner());
      }
    } else {
      int[] pos = new int[where.length+1];
      System.arraycopy(where, 0, pos, 0, where.length);
      for (int i = 0; i < shape[where.length]; i++) {
        pos[where.length] = i;
        if (i != 0) res.append(", ");
        res.append(oneliner(pos));
      }
    }
    return res + (where.length==0?"}":"]");
  }
  public Arr reverseOn(int dim) {
    if (rank == 0) {
      if (dim != 0) throw new DomainError("rotating a scalar with a non-⎕IO axis");
      return this;
    }
    if (dim < 0) dim+= rank;
    // 2×3×4:
    // 0 - 3×4s for 2
    // 1 - 4s for 3
    // 2 - 1s for 4
    int chunkS = 1;
    int cPSec = shape[dim]; // chunks per section
    for (int i = rank-1; i > dim; i--) {
      chunkS*= shape[i];
    }
    int sec = chunkS * cPSec; // section length
    Value[] res = new Value[ia];
    int c = 0;
    while (c < ia) {
      for (int i = 0; i < cPSec; i++) {
        for (int j = 0; j < chunkS; j++) {
          res[c + (cPSec-i-1)*chunkS + j] = get(c + i*chunkS + j);
        }
      }
      c+= sec;
    }
    return Arr.create(res, shape);
  }
  
  @Override
  public Value with(Value what, int[] where) { // pls override
    Value[] nvals = new Value[ia];
    System.arraycopy(values(), 0, nvals, 0, ia);
    nvals[Indexer.fromShape(shape, where, 0)] = what;
    return Arr.create(nvals, shape);
  }
  
  public static Arr create(Value[] v) {
    return create(v, new int[]{v.length});
  }
  
  public static Arr create(Value[] v, int[] sh) { // note, doesn't attempt individual item squeezing
    if (v.length == 0) return new EmptyArr(sh);
    if (v[0] instanceof Num) {
      double[] da = new double[v.length];
      for (int i = 0; i < v.length; i++) {
        if (v[i] instanceof Num) da[i] = ((Num)v[i]).num;
        else {
          da = null;
          break;
        }
      }
      if (da != null) return new DoubleArr(da, sh);
    }
    if (v[0] instanceof Char) {
      StringBuilder s = new StringBuilder();
      for (Value aV : v) {
        if (aV instanceof Char) s.append(((Char) aV).chr);
        else {
          s = null;
          break;
        }
      }
      if (s != null) return new ChrArr(s.toString(), sh);
    }
//    Value[] opt = new Value[v.length]; // do this in the caller please
//    boolean anyBetter = false;
//    for (int i = 0; i < v.length; i++) {
//      Value c = v[i];
//      Value o = c.squeeze();
//      opt[i] = o;
//      if (c != o) anyBetter = true;
//    }
//    if (anyBetter) return new HArr(opt, sh);
//    else
    return new HArr(v, sh);
  }
  
  @Override
  public boolean equals(Obj o) {
    if (!(o instanceof Arr)) return false;
    if (!Arrays.equals(shape, ((Arr) o).shape)) return false;
    Value[] mvs = values();
    Value[] ovs = ((Arr) o).values();
    for (int i = 0; i < mvs.length; i++) {
      if (!mvs[i].equals(ovs[i])) return false;
    }
    return true;
  }
  
  // note for me when transforming new HArr to Arr.create, which ends up being "new Arr.create"; ignore pls ._.
  private class delete_new_pls extends Exception{}
  public class create extends Main {create(Object...FO) throws delete_new_pls{}}
}
