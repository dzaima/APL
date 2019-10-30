package APL;

import APL.errors.*;
import APL.tokenizer.*;
import APL.tokenizer.types.BasicLines;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.io.*;
import java.util.*;

import static APL.Main.*;

public class Scope {
  private final HashMap<String, Obj> vars = new HashMap<>();
  private Scope parent = null;
  public boolean alphaDefined;
  public int IO;
  private Num nIO;
  public Random rnd;
  public Scope() {
    IO = 1;
    nIO = Num.ONE;
    rnd = new Random();
  }
  public Scope(Scope p) {
    parent = p;
    IO = p.IO;
    nIO = p.nIO;
    rnd = p.rnd;
  }
  private Scope owner(String name) {
    if (vars.containsKey(name)) return this;
    else if (parent == null) return null;
    else return parent.owner(name);
  }

  public void update (String name, Obj val) { // sets wherever var already exists
    Scope sc = owner(name);
    if (sc == null) sc = this;
    sc.set(name, val);
  }
  public void set (String name, Obj val) { // sets in current scope
    switch (name) {
      case "⎕IO":
        int tIO = ((Value) val).asInt();
        if (tIO != 0 && tIO != 1) throw new DomainError("⎕IO should be 0 or 1");
        IO = tIO;
        nIO = IO==0? Num.ZERO : Num.ONE;
      break;
      case "⎕BOXSIMPLE":
        Main.enclosePrimitives = ((Value) val).asInt() == 1;
      break;
      case "⎕VI":
        Main.vind = Main.bool(val);
      break;
      case "⎕PP":
        Num.setPrecision(((Value) val).asInt());
      break;
      default:
        vars.put(name, val);
    }
  }
  public Obj get (String name) {
    if (name.startsWith("⎕")) {
      switch (name) {
        case "⎕MILLIS": return new Num(System.currentTimeMillis() - Main.startingMillis);
        case "⎕TIME": return new Timer(this, true);
        case "⎕HTIME": return new Timer(this, false);
        case "⎕EX": return new Ex(this);
        case "⎕LNS": return new Lns();
        case "⎕SH": return new Shell();
        case "⎕A": return Main.alphabet;
        case "⎕D": return Main.digits;
        case "⎕L": return Main.lowercaseAlphabet;
        case "⎕LA": return Main.lowercaseAlphabet;
        case "⎕ERASE": return new Eraser(this);
        case "⎕GC": System.gc(); return Num.ONE;
        case "⎕DEATHLOGGER": return new DeathLogger();
        case "⎕NULL": return Null.NULL;
        case "⎕MAP": case "⎕NS": return new MapGen();
        case "⎕DL": return new Delay(this);
        case "⎕SCOPE": return new ScopeViewer(this);
        case "⎕UCS": return new UCS(this);
        case "⎕HASH": return new Hasher();
        case "⎕IO": return nIO;
        case "⎕CLASS": return new ClassGetter();
        case "⎕PP": return new Num(Num.pp);
        case "⎕PF": return new Profiler(this);
        case "⎕PFR": return Profiler.results();
        case "⎕U": return new Builtin() {
          @Override public String repr() { return "⎕U"; }
  
          @Override public Obj call(Value w) {
            Main.ucmd(Scope.this, w.asString());
            return null;
          }
        };
        case "⎕OPT": case "⎕OPTIMIZE":
          return new Optimizer(this);
      }
    }
    Obj f = vars.get(name);
    if (f == null) {
      if (parent == null) return null;
      else return parent.get(name);
    } else return f;
  }
  Variable getVar(String name) {
    return new Variable(this, name);
  }
  public String toString() {
    return toString("");
  }
  private String toString(String prep) {
    StringBuilder res = new StringBuilder("{\n");
    String cp = prep+"  ";
    for (String n : vars.keySet()) res.append(cp).append(n).append(" ← ").append(get(n)).append("\n");
    if (parent != null) res.append(cp).append("parent: ").append(parent.toString(cp));
    res.append(prep).append("}\n");
    return res.toString();
  }
  
  public double rand(double d) { // TODO seeds
    return rnd.nextDouble()*d;
  } // with ⎕IO←0
  public int rand(int n) {
    return rnd.nextInt(n);
  } // with ⎕IO←0
  
  static class DeathLogger extends Builtin {
    @Override public String repr() {
      return "⎕DEATHLOGGER";
    }
  
    @Override
    public Obj call(Value w) {
      return new DyingObj(w.toString());
    }
    class DyingObj extends Primitive {
      final String msg;
      DyingObj(String s) {
        this.msg = s;
      }
  
      @SuppressWarnings("deprecation") // as this thing is only used for debugging, this should be fine
      @Override
      protected void finalize() {
        System.out.println(msg+" died");
      }
      public String toString() {
        return "⎕DEATHLOGGER["+msg+"]";
      }
  
      @Override
      public Type type() {
        return Type.array;
      }
  
      @Override
      public Value ofShape(int[] sh) {
        throw new DomainError("you're shaping ⎕DEATHLOGGER? what?");
      }
    }
  }
  static class Timer extends Builtin {
    final boolean simple;
    @Override public String repr() {
      return "⎕TIME";
    }
    Timer(Scope sc, boolean simple) {
      super(sc);
      this.simple = simple;
    }
    public Obj call(Value w) {
      return call(Num.ONE, w);
    }
    public Obj call(Value a, Value w) {
      int[] options = a.asIntVec();
      int n = options[0];
      
      boolean testTokenizing = true;
      if (options.length >= 2) {
        testTokenizing = options[1] != 0;
      }
      
      String test = w.asString();
      long start = System.nanoTime();
      if (testTokenizing) {
        for (int i = 0; i < n; i++) Main.exec(test, sc);
      } else {
        BasicLines testTokenized = Tokenizer.tokenize(test);
        for (int i = 0; i < n; i++) execLines(testTokenized, sc);
      }
      long end = System.nanoTime();
      if (simple) {
        return new Num((end-start)/n);
      } else {
        double t = end-start;
        t/= n;
        if (t < 1000) return Main.toAPL(new Num(t)+" nanos");
        t/= 1e6;
        if (t > 500) return Main.toAPL(new Num(t/1000d)+" seconds");
        return Main.toAPL(new Num(t)+" millis");
      }
    }
  }
  static class Eraser extends Builtin {
    @Override public String repr() {
      return "⎕ERASE";
    }
    Eraser(Scope sc) {
      super(sc);
    }
    
    public Obj call(Value w) {
      sc.set(w.asString(), null);
      return w;
    }
  }
  static class Delay extends Builtin {
    @Override public String repr() {
      return "⎕DL";
    }
    Delay(Scope sc) {
      super(sc);
    }
    
    public Obj call(Value w) {
      long nsS = System.nanoTime();
      double ms = w.asDouble() * 1000;
      int ns = (int) ((ms%1)*1000000);
      try {
        Thread.sleep((int) ms, ns);
      } catch (InterruptedException ignored) { /* idk */ }
      return new Num((System.nanoTime() - nsS) / 1000000000d);
    }
  }
  static class UCS extends Builtin {
    @Override public String repr() {
      return "⎕UCS";
    }
    UCS(Scope sc) {
      super(sc);
    }
    
    public Obj call(Value w) {
      return numChrM(new NumMV() {
        @Override public Value call(Num c) {
          return new Char((char) c.asInt());
        }
  
        @Override public boolean retNum() {
          return false;
        }
      }, c->new Num(c.chr), w);
    }
  
    @Override public Obj callInv(Value w) {
      return call(w);
    }
  }
  
  static class ScopeViewer extends SimpleMap {
    
    private final Scope sc;
    
    ScopeViewer(Scope sc) {
      this.sc = sc;
    }
    
    @Override
    public Obj getv(String k) {
      if (k.equals("parent")) return new ScopeViewer(sc.parent);
      return sc.vars.get(k);
    }
    
    @Override
    public void setv(String k, Obj v) {
      throw new SyntaxError("No setting scope things!", v instanceof Value? (Value) v : null);
    }
    
    @Override
    public String toString() {
      StringBuilder res = new StringBuilder("(");
      sc.vars.forEach((key, value) -> {
        if (value instanceof ScopeViewer) return;
        if (res.length() != 1) res.append("⋄");
        res.append(key).append(":").append(value);
      });
      return res + ")";
    }
    
    @Override
    public Value ofShape(int[] sh) {
      throw new DomainError("⎕SCOPE is a debugging tool, not a toy.");
    }
  }
  
  private static class MapGen extends Builtin {
    @Override public String repr() {
      return "⎕MAP";
    }
    
    @Override
    public Obj call(Value w) {
      if (w instanceof StrMap) {
        return new StrMap(((StrMap) w));
      }
      var map = new StrMap();
      for (Value v : w) {
        if (v.rank != 1 || v.ia != 2) throw new RankError("pairs for ⎕map should be 2-item arrays", v);
        map.set(v.get(0), v.get(1));
      }
      return map;
    }
  
    @Override
    public Obj call(Value a, Value w) {
      if (a.rank != 1) throw new RankError("rank of ⍺ ≠ 1", a);
      if (w.rank != 1) throw new RankError("rank of ⍵ ≠ 1", w);
      if (a.ia != w.ia) throw new LengthError("both sides lengths should match", w);
      var map = new StrMap();
      for (int i = 0; i < a.ia; i++) {
        map.set(a.get(i), w.get(i));
      }
      return map;
    }
  }
  
  private class Optimizer extends Builtin {
    @Override public String repr() {
      return "⎕OPT";
    }
    Optimizer(Scope sc) {
      super(sc);
    }
    @Override
    public Obj call(Value w) {
      String name = w.asString();
      if (! (get(name) instanceof Value)) return Num.MINUS_ONE;
      Value v = (Value) get(name);
      Value optimized = v.squeeze();
      if (v == optimized) return Num.ZERO;
      update(name, optimized);
      return Num.ONE;
    }
  }
  private class ClassGetter extends Builtin {
    @Override public String repr() {
      return "⎕CLASS";
    }
    @Override
    public Obj call(Value w) {
      return new ChrArr(w.getClass().getCanonicalName());
    }
  }
  
  static private class Ex extends Builtin {
    @Override public String repr() {
      return "⎕EX";
    }
    Ex(Scope sc) {
      super(sc);
    }
    
    @Override
    public Obj call(Value w) {
      String path = w.asString();
      return Main.exec(Main.readFile(path), sc);
    }
  }
  static private class Lns extends Builtin {
    @Override public String repr() {
      return "⎕LNS";
    }
    
    @Override
    public Obj call(Value w) {
      String path = w.asString();
      String[] a = Main.readFile(path).split("\n");
      Value[] o = new Value[a.length];
      for (int i = 0; i < a.length; i++) {
        o[i] = Main.toAPL(a[i]);
      }
      return Arr.create(o);
    }
  }
  
  
  private class Shell extends Fun {
    @Override public String repr() {
      return "⎕SH";
    }
  
    @Override
    public Obj call(Value w) {
      return exec(w, null);
    }
  
    @Override
    public Obj call(Value a, Value w) {
      return exec(w, new File(a.asString()));
    }
    
    public Obj exec(Value w, File f) {
      try {
        Process p;
        if (w.get(0) instanceof Char) {
          String cmd = w.asString();
          p = Runtime.getRuntime().exec(cmd, new String[0], f);
        } else {
          String[] parts = new String[w.ia];
          for (int i = 0; i < parts.length; i++) {
            parts[i] = w.get(i).asString();
          }
          p = Runtime.getRuntime().exec(parts, new String[0], f);
        }
        return new Num(p.waitFor());
      } catch (Throwable e) {
        e.printStackTrace();
        return Null.NULL;
      }
    }
  }
  
  
  static private class Hasher extends Builtin {
    @Override public String repr() {
      return "⎕HASH";
    }
    @Override
    public Obj call(Value w) {
      return new Num(w.hashCode());
    }
  }
  
  private static class Profiler extends Builtin {
    Profiler(Scope sc) {
      super(sc);
    }
    
    static HashMap<String, Pr> pfRes = new HashMap<>();
    static double cam = 0;
    public static Obj results() {
      Value[] arr = new Value[pfRes.size()*4];
      final int[] p = {0};
      cam++;
      pfRes.forEach((s, pr) -> {
        arr[p[0]++] = toAPL(s);
        arr[p[0]++] = new Num(pr.am/cam);
        arr[p[0]++] = new Num(pr.ms/cam);
        arr[p[0]++] = new Num(pr.ms/pr.am);
        if (cam > 100) {
          pr.am = 0;
          pr.ms = 0;
        }
      });
      if (cam > 100) cam = 0;
      return new HArr(arr, new int[]{pfRes.size(), 4});
    }
  
    @Override public String repr() {
      return "⎕PF";
    }
    @Override
    public Obj call(Value w) {
      return call(w, w);
    }
    @Override
    public Obj call(Value a, Value w) {
      String s = w.asString();
      String k = a.asString();
      if (!pfRes.containsKey(k)) pfRes.put(k, new Pr(Tokenizer.tokenize(s)));
      Pr p = pfRes.get(k);
      BasicLines t = p.tok;
      
      p.am++;
      long ns = System.nanoTime();
      Obj res = execLines(t, sc);
      long rns = System.nanoTime() - ns;
      p.ms+= rns/1000000d;
      return res;
    }
  }
  static class Pr {
    private final BasicLines tok;
    int am;
    double ms;
  
    public Pr(BasicLines tok) {
      this.tok = tok;
    }
  }
  
}