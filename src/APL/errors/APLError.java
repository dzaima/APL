package APL.errors;

import APL.tokenizer.Token;
import APL.types.*;

import java.util.ArrayList;

import static APL.Main.*;

public abstract class APLError extends RuntimeException {
  public Tokenable cause;
  
  APLError(String msg) {
    super(msg);
  }
  APLError(String msg, Tokenable blame) {
    super(msg);
    if (blame instanceof Callable) faulty = blame;
    else cause = blame;
  }
  APLError(String msg, Callable blame, Tokenable cause) {
    super(msg);
    faulty = blame;
    this.cause = cause;
  }
  
  
  public void print() {
    String type = getClass().getSimpleName();
    if (getMessage().length() == 0) colorprint(type, 246);
    else colorprint(type + ": " + getMessage(), 246);
    ArrayList<Mg> l = new ArrayList<>();
    if (faulty!=null) {
      l.add(new Mg(faulty, '^'));
    }
    if (cause!=null && (faulty==null || faulty.getToken()!=cause.getToken())) {
      l.add(new Mg(cause, '¯'));
    }
    for (Mg g : l) {
      Token t = g.t.getToken();
      if (t == null) continue;
      int spos = t.spos;
      int epos = t.epos==Token.EPOS_DEF? spos+1 : t.epos;
      String start = t.raw.substring(0, spos);
      int lnn = start.split("\n").length-1;
      String ln = t.raw.split("\n")[lnn==-1? 0 : lnn];
      int lns = start.lastIndexOf('\n')+1;
      //println(t.raw+" "+spos+" "+epos);
      println(ln);
      //println(g.c +" "+ faulty);
      //println();
      int rs = spos-lns;
      int re = epos-lns;
      StringBuilder b = new StringBuilder();
      for (int i = 0 ; i < re; i++) {
        b.append(i>=rs? g.c : ' ');
      }
      println(b.toString());
    }
  }
  static class Mg {
    final Tokenable t;
    final char c;
    
    Mg(Tokenable t, char c) {
      this.t = t;
      this.c = c;
    }
  }
}