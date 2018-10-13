package APL.types;

import APL.*;
import APL.errors.DomainError;

import java.util.*;
import java.util.stream.*;

public class Arr extends Value {
  private static final Arr errNull = Main.toAPL("JAVANULL", new Token(TType.str, 0, "JAVANULL"));
  
  public Arr (Value prototype) {
    this(new Value[0]);
    this.prototype = prototype;
  }
  public Arr (Value[] v, int[] sh) {
    ia = v.length;
    shape = sh;
    rank = sh.length;
    arr = v;
    if (ia >= 1) prototype = v[0] instanceof Char? Char.SPACE : Num.ZERO;
  }
  public Arr (Value[] v, int[] sh, Value prototype) {
    ia = v.length;
    shape = sh;
    rank = sh.length;
    arr = v;
    this.prototype = prototype;
  }
  public Arr (ArrayList<Value> v) { // 1D
    this(v.toArray(new Value[0]));
  }
  public Arr (Value[] v) { // 1D
    ia = v.length;
    shape = new int[]{ia};
    rank = 1;
    arr = v;
    if (ia >= 1) prototype = v[0] instanceof Char? Char.SPACE : Num.ZERO;
  }
  public String string(boolean quote) {
    if (rank == 1/* && shape[0] != 1*/) { // strings
      StringBuilder all = new StringBuilder();
      for (Value v : arr) {
        if (v instanceof Char) {
          char c = ((Char)v).chr;
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
      if (rank == 1) return prototype == Num.ZERO? "⍬" : prototype instanceof Char? "''" : "?";
      else {
        String s = IntStream.range(0, rank).mapToObj(i -> String.valueOf(shape[i])).collect(Collectors.joining(" "));
        return s + "⍴" + (prototype == Num.ZERO? "⍬" : prototype instanceof Char? "''" : "?");
      }
    }
    String qs = string(Main.quotestrings || Main.prettyprint);
    if (qs != null) return qs;
    if (Main.prettyprint) {
      if (rank == 0) return "⊂" + oneliner(new int[0]);
      return oneliner(new int[0]);
    } else {
      if (rank == 0 && !primitive()) return "⊂"+first().toString();
      if (rank == 1) {
        StringBuilder res = new StringBuilder();
        var simple = true;
        for (Value v : arr) {
          if (res.length() > 0) res.append(" ");
          if (v == null) {
            res.append("JAVANULL");
          } else {
            simple &= v.primitive();
            res.append(v.toString());
          }
        }
        if (simple) return res.toString();
      }
      if (rank < 3) {
        int w = rank==1? shape[0] : shape[1];
        int h = rank==1? 1 : shape[0];
        String[][][] stringified = new String[w][h][];
        int[][] itemWidths = new int[w][h];
        int[] widths = new int[w];
        int[] heights = new int[h];
        var simple = true;
        int x=0, y=0;
        for (Value v : arr) {
          if (v == null) v = Arr.errNull;
          simple &= v.primitive();
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
              int sx = arr[y*w + x] instanceof Num? rx+widths[x]-itemWidths[x][y] : rx;
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
      } else return oneliner(new int[0]);
    }
  }
  String oneliner(int[] where) {
    var qs = string(true);
    if (qs != null) return qs;
    StringBuilder res = new StringBuilder(where.length == 0 ? "{" : "[");
    if (rank == 0) {
      return first().oneliner(new int[0]);
    } else if (where.length == rank-1) {
      int[] pos = new int[rank];
      System.arraycopy(where, 0, pos, 0, where.length);
      for (int i = 0; i < shape[where.length]; i++) {
        pos[rank-1] = i;
        if (i != 0) res.append(", ");
        res.append(simpleAt(pos).oneliner(new int[0]));
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
//    System.out.println(chunkS+" "+cPSec+" "+sec);
    Value[] res = new Value[ia];
    int c = 0;
    while (c < ia) {
      for (int i = 0; i < cPSec; i++) {
        for (int j = 0; j < chunkS; j++) {
//          System.out.println(c+" "+i+" "+j);
          res[c + (cPSec-i-1)*chunkS + j] =
            arr[c + i*chunkS + j];
        }
      }
      c+= sec;
    }
    return new Arr(res, shape);
  }
  
  @Override
  public boolean equals(Obj o) {
    if (!(o instanceof Value)) return false;
    Value v = (Value) o;
    if (!Arrays.equals(shape, v.shape)) return false;
    assert ia == v.ia;
    return IntStream.range(0, ia).allMatch(i -> arr[i].equals(v.arr[i]));
  }
  
  public String fromAPL() {
    if (!Arrays.stream(arr).allMatch(c -> c instanceof Char)) throw new DomainError("Converting non-char array to string");
    return Arrays.stream(arr).map(Value::fromAPL).collect(Collectors.joining());
  }
  
  private Integer hashCode;
  @Override
  public int hashCode() {
    if (hashCode == null) {
      hashCode = Arrays.hashCode(arr);
      hashCode^= Arrays.hashCode(shape);
    }
    return hashCode;
  }
}
