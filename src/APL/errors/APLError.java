package APL.errors;

import APL.types.Obj;

import java.util.stream.*;

import static APL.Main.*;

public class APLError extends Error {
  Obj fn;
  public Obj cause;
  APLError (String msg) {
    super(msg);
  }
  public void print() {
    String[] ns = getClass().getName().split("[$.]");
    if (getMessage().length() == 0) colorprint(ns[ns.length - 1], 246);
    else colorprint(ns[ns.length - 1] + ": " + getMessage(), 246);
    String fnline = null;
    String oline = null;
    int fnpos = 0;
    int opos = 0;
    if (fn != null && fn.token != null) {
      fnline = fn.token.line;
      fnpos = fn.token.pos;
    }
    if (cause != null && cause.token != null) {
      oline = cause.token.line;
      opos = cause.token.pos;
    }
    if (oline != null) {
      if (oline.equals(fnline)) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < oline.length(); i++) {
          s.append(i == fnpos ? '^' : i == opos ? '¯' : ' ');
        }
        colorprint(oline, 217);
        colorprint(s.toString(), 217);
      }
    } else if (fnline != null) {
      String s = IntStream.range(0, fnpos).mapToObj(i -> " ").collect(Collectors.joining());
      colorprint(fnline, 217);
      colorprint(s +"^", 217);
    }
  }
}
