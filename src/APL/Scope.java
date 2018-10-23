package APL;

import APL.errors.*;
import APL.types.*;
import APL.types.functions.Builtin;

import java.util.HashMap;

public class Scope {
  private final HashMap<String, Obj> vars = new HashMap<>();
  private Scope parent = null;
  public boolean alphaDefined;
  public Scope() {
    vars.put("⎕IO", new Num("1"));
    vars.put("⎕COND", Main.toAPL("01", new Token(TType.str, "01",0, "'01'")));
  }
  public Scope(Scope p) {
    parent = p;
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
    if (name.equals("⎕COND")) {
      if (! (val instanceof Arr)) throw new DomainError("setting ⎕COND to " + Main.human(val.type()));
      String s = ((Arr)val).string(false);
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
  }
  public double rand(int n) {
    return Math.floor(Math.random() * n);
  }
  
  static class DeathLogger extends Builtin {
    DeathLogger() {
      super("⎕DEATHLOGGER", 0x001);
    }
  
    @Override
    public Obj call(Value w) {
      return new DyingObj(w.toString());
    }
    class DyingObj extends Value {
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
      int n = ((Num) a).intValue();
      long start = System.nanoTime();
      for (int i = 0; i < n; i++) Main.exec(w.fromAPL(), sc);
      long end = System.nanoTime();
      if (simple) return new Num((end-start)/n);
      else {
        double t = end-start;
        t/= n;
        if (t < 1000) return Main.toAPL(t+" nanos", new Token(TType.expr, "nanos", 0, "the thing that made ⎕htime"));
        t/= 1e6;
        if (t > 500) return Main.toAPL((t/1000d)+" seconds", new Token(TType.expr, "seconds", 0, "the thing that made ⎕htime"));
        return Main.toAPL(t+" millis", new Token(TType.expr, "millis", 0, "the thing that made ⎕htime"));
      }
    }
  }
  static class Eraser extends Builtin {
    Eraser(Scope sc) {
      super("⎕ERASE", 0x001, sc);
    }
    
    public Obj call(Value w) {
      sc.set(w.fromAPL(), null);
      return w;
    }
  }
  
  class ScopeViewer extends APLMap {
  
    private final Scope sc;
  
    ScopeViewer(Scope sc) {
      this.sc = sc;
    }
  
    @Override
    public Obj getRaw(Value k) {
      String s = k.fromAPL();
      if (s.equals("parent")) return new ScopeViewer(sc.parent);
      return sc.vars.get(s);
    }
  
    @Override
    public void set(Value k, Obj v) {
      throw new SyntaxError("No setting scope things!", this, v instanceof Value? (Value) v : null);
    }
  
    @Override
    public Arr toArr() {
      throw new SyntaxError("scope to array", this);
    }
  
    @Override
    public int size() {
      throw new SyntaxError("size of scope", this);
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
  }
  
  private class MapGen extends Builtin {
  
    MapGen() {
      super("⎕MAP", 0x011);
    }
  
    @Override
    public Obj call(Value w) {
      var map = new StrMap();
      for (Value v : w.arr) {
        if (v.rank != 1 || v.ia != 2) throw new RankError("pairs for ⎕smap should be 2-item arrays", this, v);
        map.set(v.arr[0], v.arr[1]);
      }
      return map;
    }
  
    @Override
    public Obj call(Value a, Value w) {
      if (a.rank != 1) throw new RankError("rank of ⍺ ≠ 1", this, a);
      if (w.rank != 1) throw new RankError("rank of ⍵ ≠ 1", this, w);
      if (a.ia != w.ia) throw new LengthError("both sides lengths should match", this, w);
      var map = new StrMap();
      for (int i = 0; i < a.ia; i++) {
        map.set(a.arr[i], w.arr[i]);
      }
      return map;
    }
  }
}