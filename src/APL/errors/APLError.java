package APL.errors;

import APL.*;
import APL.types.Obj;

import java.util.stream.*;

import static APL.Main.*;

public class APLError extends Error {
  public Obj cause;
  Token t;
  APLError (String msg, Token t) {
    super(msg);
    this.t = t;
  }
  APLError (String msg) {
    super(msg);
  }
  public void print() {
    String[] ns = getClass().getName().split("[$.]");
    if (getMessage().length() == 0) colorprint(ns[ns.length - 1], 246);
    else colorprint(ns[ns.length - 1] + ": " + getMessage(), 246);
    String oline = null;
    int opos = 0;
    if (cause != null && cause.token != null) {
      oline = cause.token.line;
      opos = cause.token.pos;
    }
    if (Main.faulty == null || Main.faulty.token == null) {
      String s = IntStream.range(0, opos).mapToObj(i -> " ").collect(Collectors.joining());
      colorprint(oline, 217);
      colorprint(s +"^", 217);
    } else {
      String fnline = Main.faulty.token.line;
      int fnpos = Main.faulty.token.pos;
      if (oline != null && oline.equals(fnline)) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < oline.length(); i++) {
          s.append(i == fnpos? '^' : i == opos? '¯' : ' ');
        }
        colorprint(oline, 217);
        colorprint(s.toString(), 217);
      } else {
        String s = IntStream.range(0, fnpos).mapToObj(i -> " ").collect(Collectors.joining());
        colorprint(fnline, 217);
        colorprint(s + "^", 217);
      }
    }
  }
}
