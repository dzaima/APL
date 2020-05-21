package APL.errors;

import APL.Main;
import APL.tokenizer.Token;
import APL.types.*;

import java.util.*;

import static APL.Main.*;

public abstract class APLError extends RuntimeException {
  public Tokenable cause;
  
  protected APLError(String msg) {
    super(msg);
  }
  protected APLError(String msg, Tokenable blame) {
    super(msg);
    if (blame instanceof Callable) faulty = blame;
    else cause = blame;
  }
  protected APLError(String msg, Callable blame, Tokenable cause) {
    super(msg);
    faulty = blame;
    this.cause = cause;
  }
  
  
  public void print() {
    String type = getClass().getSimpleName();
    if (getMessage().length() == 0) colorprint(type, 246);
    else colorprint(type + ": " + getMessage(), 246);
    ArrayList<Mg> l = new ArrayList<>();
    if (faulty!=null) Mg.add(l, faulty, '^');
    if (cause !=null) Mg.add(l, cause , '¯');
    if (l.size() == 2 && l.get(0).eqSrc(l.get(1))) println(l);
    else for (Mg g : l) println(List.of(g));
  }
  
  public void println(List<Mg> gs) {
    if (gs.size() == 0) return;
    
    String raw = gs.get(0).raw;
    int lns = gs.get(0).lns;
    
    int lne = raw.indexOf("\n", lns);
    if (lne == -1) lne = raw.length();
    
    String ln = gs.get(0).raw.substring(lns, lne);
    Main.println(ln);
    char[] str = new char[ln.length()];
    for (int i = 0; i < str.length; i++) {
      char c = ' ';
      for (Mg g : gs) if (i>=g.spos && i<g.epos) c = g.c;
      str[i] = c;
    }
    Main.println(new String(str));
  }
  
  static class Mg {
    final Token t;
    final char c;
    final String raw;
    int lns;
    int spos, epos; // in the line
  
    public Mg(Token t, char c, String raw, int lns, int spos, int epos) {
      this.t = t;
      this.c = c;
      this.raw = raw;
      this.lns = lns;
      this.spos = spos;
      this.epos = epos;
    }
  
    static void add(ArrayList<Mg> l, Tokenable to, char c) {
      Token t = to.getToken();
      if (t == null) return;
      
      String raw = t.raw;
  
      int lns = raw.lastIndexOf("\n", t.spos) + 1; // not found handles itself
  
  
      int spos = t.spos - lns;
      int epos = (t.epos==Token.EPOS_DEF? spos+1 : t.epos) - lns;
      
      l.add(new Mg(t, c, raw, lns, spos, epos));
    }
    
    boolean eqSrc(Mg g) {
      // noinspection StringEquality \\ we want that
      return raw==g.raw && lns==g.lns;
    }
  }
}