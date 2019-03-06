package APL.errors;

import APL.*;
import APL.types.*;

import java.util.stream.*;

import static APL.Main.colorprint;

public class APLError extends Error {
  public Tokenable cause;
  
  APLError (String msg) {
    super(msg);
  }
  public void print() {
    String[] ns = getClass().getName().split("[$.]");
    if (getMessage().length() == 0) colorprint(ns[ns.length - 1], 246);
    else colorprint(ns[ns.length - 1] + ": " + getMessage(), 246);
    String oline = null;
    int opos = 0;
    if (cause != null && cause.getToken() != null) {
      oline = cause.getToken().line;
      opos = cause.getToken().pos;
    }
    if (Main.faulty == null || Main.faulty.getToken() == null) { // fn bad
      if (oline == null) return; // both bad
      String s = IntStream.range(0, opos).mapToObj(i -> " ").collect(Collectors.joining());
      colorprint(oline, 217);
      colorprint(s +"^", 217);
    } else { // fn good
      String fnline = Main.faulty.getToken().line;
      int fnpos = Main.faulty.getToken().pos;
      if (oline != null && oline.equals(fnline)) { // draw both
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < oline.length(); i++) {
          s.append(i == fnpos? '^' : i == opos? '¯' : ' ');
        }
        colorprint(oline, 217);
        colorprint(s.toString(), 217);
      } else { // only fn
        String s = IntStream.range(0, fnpos).mapToObj(i -> " ").collect(Collectors.joining());
        colorprint(fnline, 217);
        colorprint(s + "^", 217);
      }
    }
  }
}
