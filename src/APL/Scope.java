package APL;

import APL.errors.*;
import APL.types.*;
import APL.types.arrs.HArr;
import APL.types.functions.Builtin;

import java.util.HashMap;

public class Scope {
  private final HashMap<String, Obj> vars = new HashMap<>();
  private Scope parent = null;
  public boolean alphaDefined;
  public int IO;
  private Num nIO;
  public Scope() {
    IO = 1;
    nIO = Num.ONE;
    vars.put("⎕COND", Main.toAPL("01"));
  }
  public Scope(Scope p) {
    parent = p;
    IO = p.IO;
    nIO = p.nIO;
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
    if (name.equals("⎕IO")) {
      IO = ((Value)val).asInt();
      nIO = IO==0? Num.ZERO : Num.ONE;
    }
    if (name.equals("⎕COND")) {
      String s = ((Arr) val).asString();
      if (s == null) throw new DomainError("⎕COND must be set to a character vector");
      String m = s.endsWith(" ")? s.substring(0, s.length()-1) : s;
      if (!m.equals("01") && !m.equals(">0") && !m.equals("≠0")) {
        throw new DomainError("⎕COND must be one of '01', '>0', '≠0' optionally followed by ' ' if space should be falsy");
      }
    }
    vars.put(name, val);
  }
  public Obj get (String name) {
    if (name.startsWith("⎕")) {
      switch (name) {
        case "⎕MILLIS": return new Num(System.currentTimeMillis() - Main.startingMillis);
        case "⎕TIME": return new Timer(this, true);
        case "⎕HTIME": return new Timer(this, false);
        case "⎕A": return Main.alphabet;
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
    DeathLogger() {
      super("⎕DEATHLOGGER", 0x001);
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
    Timer(Scope sc, boolean simple) {
      super("⎕TIME", 0x001, sc);
      this.simple = simple;
    }
    public Obj call(Value w) {
      return call(Num.ONE, w);
    }
    public Obj call(Value a, Value w) {
      int n = a.asInt();
      long start = System.nanoTime();
      for (int i = 0; i < n; i++) Main.exec(w.asString(), sc);
      long end = System.nanoTime();
      if (simple) return new Num((end-start)/n);
      else {
        double t = end-start;
        t/= n;
        if (t < 1000) return Main.toAPL(t+" nanos");
        t/= 1e6;
        if (t > 500) return Main.toAPL((t/1000d)+" seconds");
        return Main.toAPL(t+" millis");
      }
    }
  }
  static class Eraser extends Builtin {
    Eraser(Scope sc) {
      super("⎕ERASE", 0x001, sc);
    }
    
    public Obj call(Value w) {
      sc.set(w.asString(), null);
      return w;
    }
  }
  static class UCS extends Builtin {
    UCS(Scope sc) {
      super("⎕UCS", 0x001, sc);
    }
    
    public Obj call(Value w) {
      return numChr(c->new Char((char)c.asInt()), c->new Num(c.chr), w);
    }
  }
  
  class ScopeViewer extends APLMap {
  
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
    public HArr toArr() {
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
  
  private class MapGen extends Builtin {
  
    MapGen() {
      super("⎕MAP", 0x011);
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
}