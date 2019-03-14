package APL;

import APL.errors.*;
import APL.tokenizer.*;
import APL.tokenizer.types.BasicLines;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.Builtin;

import java.util.HashMap;

public class Scope {
  private final HashMap<String, Obj> vars = new HashMap<>();
  private Scope parent = null;
  public boolean alphaDefined;
  public int IO;
  private Num nIO;
  
  enum Cond {
    _01, gt0, ne0,
  }
  boolean condSpaces;
  Cond cond;
  public Scope() {
    IO = 1;
    nIO = Num.ONE;
    cond = Cond._01;
    condSpaces = false;
  }
  public Scope(Scope p) {
    parent = p;
    IO = p.IO;
    nIO = p.nIO;
    cond = p.cond;
    condSpaces = p.condSpaces;
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
        IO = ((Value) val).asInt();
        nIO = IO==0? Num.ZERO : Num.ONE;
      break;
      case "⎕BOXSIMPLE":
        Main.enclosePrimitives = ((Value) val).asInt() == 1;
      break;
      case "⎕VI":
        Main.vind = Main.bool(val, this);
      break;
      case "⎕COND":
        String s = ((Arr) val).asString();
        if (s == null) throw new DomainError("⎕COND must be set to a character vector");
        switch (s) {
          case "01" : cond = Cond._01; condSpaces = false; return;
          case ">0" : cond = Cond.gt0; condSpaces = false; return;
          case "≠0" : cond = Cond.ne0; condSpaces = false; return;
          case "01 ": cond = Cond._01; condSpaces = true ; return;
          case ">0 ": cond = Cond.gt0; condSpaces = true ; return;
          case "≠0 ": cond = Cond.ne0; condSpaces = true ; return;
          default: throw new DomainError("⎕COND must be one of '01', '>0', '≠0' optionally followed by ' ' if space should be falsy");
        }
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
        case "⎕A": return Main.alphabet;
        case "⎕D": return Main.digits;
        case "⎕L": return Main.lowercaseAlphabet;
        case "⎕LA": return Main.lowercaseAlphabet;
        case "⎕ERASE": return new Eraser(this);
        case "⎕GC": System.gc(); return Num.ONE;
        case "⎕DEATHLOGGER": return new DeathLogger();
        case "⎕NULL": return Null.NULL;
        case "⎕MAP": case "⎕NS": return new MapGen();
        case "⎕SCOPE": return new ScopeViewer(this);
        case "⎕UCS": return new UCS(this);
        case "⎕IO": return nIO;
        case "⎕CLASS": return new ClassGetter();
        case "⎕PP": return new Num(Num.pp);
        case "⎕COND": switch (cond) {
          case _01: if (condSpaces) return new ChrArr("01 "); return new ChrArr("01");
          case gt0: if (condSpaces) return new ChrArr(">0 "); return new ChrArr(">0");
          case ne0: if (condSpaces) return new ChrArr("≠0 "); return new ChrArr("≠0");
        }
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
    return Math.random() * d;
  } // with ⎕IO←0
  public double rand(int n) {
    return Math.floor(Math.random() * n);
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
        for (int i = 0; i < n; i++) Main.execLines(testTokenized, sc);
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
  }
  
  static class ScopeViewer extends APLMap {
  
    private final Scope sc;
  
    ScopeViewer(Scope sc) {
      this.sc = sc;
    }
  
    @Override
    public Obj getRaw(Value k) {
      String s = k.asString();
      if (s.equals("parent")) return new ScopeViewer(sc.parent);
      return sc.vars.get(s);
    }
  
    @Override
    public void set(Value k, Obj v) {
      throw new SyntaxError("No setting scope things!", v instanceof Value? (Value) v : null);
    }
  
    @Override
    public Arr toArr() {
      throw new SyntaxError("scope to array", this);
    }
  
    @Override
    public int size() {
      throw new DomainError("size of ⎕SCOPE");
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
      var map = new StrMap();
      for (Value v : w) {
        if (v.rank != 1 || v.ia != 2) throw new RankError("pairs for ⎕smap should be 2-item arrays", v);
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
      return "⎕OPTIMIZE";
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
}