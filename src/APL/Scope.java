package APL;

import APL.errors.*;
import APL.tokenizer.Tokenizer;
import APL.tokenizer.types.BasicLines;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.*;
import APL.types.functions.builtins.dops.OverBuiltin;
import APL.types.functions.builtins.fns.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Scope {
  public final HashMap<String, Obj> vars = new HashMap<>();
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
  
  public void update(String name, Obj val) { // sets wherever var already exists
    Scope sc = owner(name);
    if (sc == null) sc = this;
    sc.set(name, val);
  }
  public void set(String name, Obj val) { // sets in current scope
    if (name.charAt(0) == '⎕') {
      switch (name) {
        case "⎕IO":
          int tIO = ((Value) val).asInt();
          if (tIO != 0 && tIO != 1) throw new DomainError("⎕IO should be 0 or 1", val);
          IO = tIO;
          nIO = IO==0? Num.ZERO : Num.ONE;
          break;
        case "⎕BOXSIMPLE":
          Main.enclosePrimitives = ((Value) val).asInt() == 1;
          break;
        case "⎕VI":
          Main.vind = Main.bool(val);
          break;
        case "⎕RL":
          rnd = new Random(((Value) val).asInt());
          break;
        case "⎕PP":
          if (val instanceof Primitive) {
            Num.setPrecision(((Value) val).asInt());
          } else {
            int[] args = ((Value) val).asIntVec();
            if (args.length == 3) Num.setPrecision(args[0], args[1], args[2]);
            else throw new DomainError("⎕PP expected either a scalar number or array of 3 integers as ⍵", val);
          }
          break;
        default:
          throw new DomainError("setting unknown quad "+name);
      }
    } else vars.put(name, val);
  }
  public Obj get(String name) {
    if (name.startsWith("⎕")) {
      switch (name) {
        case "⎕MILLIS": return new Num(System.currentTimeMillis() - Main.startingMillis);
        case "⎕TIME": return new Timer(this, true);
        case "⎕HTIME": return new Timer(this, false);
        case "⎕EX": return new Ex(this);
        case "⎕LNS": return new Lns();
        case "⎕SH": return new Shell();
        case "⎕NC": return new NC();
        case "⎕A": return Main.alphabet;
        case "⎕AV": return Main.toAPL(Main.CODEPAGE);
        case "⎕D": return Main.digits;
        case "⎕L":
        case "⎕LA": return Main.lowercaseAlphabet;
        case "⎕ERASE": return new Eraser(this);
        case "⎕GC": System.gc(); return new Num(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        case "⎕GCLOG": return new GCLog();
        case "⎕NULL": return Null.NULL;
        case "⎕MAP": case "⎕NS": return new MapGen();
        case "⎕DL": return new Delay(this);
        case "⎕DR": return new DR();
        case "⎕UCS": return new UCS(this);
        case "⎕HASH": return new Hasher();
        case "⎕IO": return nIO;
        case "⎕VI": return Main.vind? Num.ONE : Num.ZERO;
        case "⎕BOXSIMPLE": return Main.enclosePrimitives? Num.ONE : Num.ZERO;
        case "⎕CLASS": return new ClassGetter();
        case "⎕PP": return new DoubleArr(new double[] {Num.pp, Num.sEr, Num.eEr});
        case "⎕PFX": return new Profiler(this);
        case "⎕PFO": return new Profiler.ProfilerOp(this);
        case "⎕PFR": return Profiler.results();
        case "⎕STDIN": return new Stdin();
        case "⎕BIG": return new Big();
        case "⎕U": return new Builtin() {
          @Override public String repr() { return "⎕U"; }
  
          @Override public Value call(Value w) {
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
  
  public double rand(double d) {
    return rnd.nextDouble()*d;
  } // with ⎕IO←0
  public long randLong() {
    return rnd.nextLong();
  } // with ⎕IO←0
  public int rand(int n) {
    return rnd.nextInt(n);
  } // with ⎕IO←0
  
  static class GCLog extends Builtin {
    @Override public String repr() {
      return "⎕GCLOG";
    }
    
    @Override
    public Value call(Value w) {
      return new Logger(w.toString());
    }
    static class Logger extends Primitive {
      final String msg;
      Logger(String s) {
        this.msg = s;
      }
      
      @SuppressWarnings("deprecation") // this is this things purpose
      @Override
      protected void finalize() {
        Main.println(msg+" was GCed");
      }
      public String toString() {
        return "⎕GCLOG["+msg+"]";
      }
      
      @Override
      public Value ofShape(int[] sh) {
        return SingleItemArr.maybe(this, sh);
      }
    }
  }
  static class Timer extends Builtin {
    final boolean raw;
    @Override public String repr() {
      return "⎕TIME";
    }
    Timer(Scope sc, boolean raw) {
      super(sc);
      this.raw = raw;
    }
    public Value call(Value w) {
      return call(Num.ONE, w);
    }
    public Value call(Value a, Value w) {
      int[] options = a.asIntVec();
      int n = options[0];
      
      boolean separate = false;
      if (options.length >= 2) separate = options[1]==1;
      
      
      String test = w.asString();
      
      BasicLines testTokenized = Tokenizer.tokenize(test);
      
      if (separate) {
        double[] r = new double[n];
        for (int i = 0; i < n; i++) {
          long start = System.nanoTime();
          Main.execLines(testTokenized, sc);
          long end = System.nanoTime();
          r[i] = end-start;
        }
        return new DoubleArr(r);
      } else {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) Main.execLines(testTokenized, sc);
        long end = System.nanoTime();
        if (raw) {
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
  }
  static class Eraser extends Builtin {
    @Override public String repr() {
      return "⎕ERASE";
    }
    Eraser(Scope sc) {
      super(sc);
    }
    
    public Value call(Value w) {
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
    
    public Value call(Value w) {
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
    
    public Value call(Value w) {
      return numChrM(new NumMV() {
        @Override public Value call(Num c) {
          return Char.of((char) c.asInt());
        }
  
        @Override public boolean retNum() {
          return false;
        }
      }, c->Num.of(c.chr), w);
    }
    
    @Override public Value callInv(Value w) {
      return call(w);
    }
  }
  
  private static class MapGen extends Builtin {
    @Override public String repr() {
      return "⎕MAP";
    }
    
    @Override
    public Value call(Value w) {
      if (w instanceof StrMap) {
        StrMap wm = (StrMap) w;
        // Scope sc;
        // HashMap<String, Obj> vals;
        // if (wm.sc == null) {
        //   sc = null;
        //   vals = new HashMap<>(wm.vals);
        // } else {
        //   sc = new Scope(wm.sc.parent);
        //   sc.vars.putAll(wm.vals);
        //   vals = sc.vars;
        // }
        // return new StrMap(sc, vals);
        return new StrMap(new HashMap<>(wm.vals));
      }
      var map = new StrMap();
      for (Value v : w) {
        if (v.rank != 1 || v.ia != 2) throw new RankError("⎕map: input pairs should be 2-item vectors", this, v);
        map.set(v.get(0), v.get(1));
      }
      return map;
    }
    
    @Override
    public Value call(Value a, Value w) {
      if (a.rank != 1) throw new RankError("rank of ⍺ ≠ 1", this, a);
      if (w.rank != 1) throw new RankError("rank of ⍵ ≠ 1", this, w);
      if (a.ia != w.ia) throw new LengthError("both sides lengths should match", this, w);
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
    public Value call(Value w) {
      String name = w.asString();
      if (!(get(name) instanceof Value)) return Num.MINUS_ONE;
      Value v = (Value) get(name);
      Value optimized = v.squeeze();
      if (v == optimized) return Num.ZERO;
      update(name, optimized);
      return Num.ONE;
    }
  }
  private static class ClassGetter extends Builtin {
    @Override public String repr() {
      return "⎕CLASS";
    }
    @Override
    public Value call(Value w) {
      return new ChrArr(w.getClass().getCanonicalName());
    }
  }
  
  private static class Ex extends Builtin {
    @Override public String repr() {
      return "⎕EX";
    }
    Ex(Scope sc) {
      super(sc);
    }
    
    public Value call(Value w) {
      Obj o = callObj(w);
      if (o instanceof Value) return (Value) o;
      throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
    }
    public Obj callObj(Value w) {
      String path = w.asString();
      return Main.exec(Main.readFile(path), sc);
    }
  }
  private static class Lns extends Builtin {
    @Override public String repr() {
      return "⎕LNS";
    }
    
    @Override
    public Value call(Value w) {
      String path = w.asString();
      String[] a = Main.readFile(path).split("\n");
      Value[] o = new Value[a.length];
      for (int i = 0; i < a.length; i++) {
        o[i] = Main.toAPL(a[i]);
      }
      return Arr.create(o);
    }
    
    String get(APLMap m, String key, String def) {
      Value got = (Value) m.getRaw(key);
      if (got != Null.NULL) return got.asString();
      return def;
    }
    
    @Override public Value call(Value a, Value w) {
      if (a instanceof APLMap) {
        try {
          URL url = new URL(w.asString());
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          APLMap m = (APLMap) a;
          String content = get(m, "content", "");
          conn.setRequestMethod(get(m, "method", "POST"));
          
          conn.setRequestProperty("Content-Type", get(m, "type", "POST"));
          conn.setRequestProperty("Content-Language", get(m, "language", "en-US"));
          conn.setRequestProperty("Content-Length", Integer.toString(content.length()));
          
          Obj eo = m.getRaw("e");
          if (eo != Null.NULL) {
            APLMap e = (APLMap) eo;
            for (Value k : e.allKeys()) {
              Value v = (Value) e.getRaw(k);
              conn.setRequestProperty(k.asString(), v.asString());
            }
          }
          
          Obj cache = m.getRaw("cache");
          conn.setUseCaches(cache!=Null.NULL && Main.bool(cache));
          conn.setDoOutput(true);
          
          if (content.length() != 0) {
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(content);
            os.close();
          }
          
          
          InputStream is = conn.getInputStream();
          ArrayList<Value> vs = new ArrayList<>();
          try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
            String ln;
            while ((ln = rd.readLine()) != null) vs.add(Main.toAPL(ln));
          }
          return new HArr(vs);
        } catch (MalformedURLException e) {
          throw new DomainError("bad URL: "+e.getMessage(), this);
        } catch (ProtocolException e) {
          throw new DomainError("ProtocolException: "+e.getMessage(), this);
        } catch (IOException e) {
          throw new DomainError("IOException: "+e.getMessage(), this);
        }
      } else {
        String p = a.asString();
        String s = w.asString();
        try (PrintWriter pw = new PrintWriter(p)) {
          pw.write(s);
        } catch (FileNotFoundException e) {
          throw new DomainError("File "+p+" not found: "+e.getMessage(), this);
        }
        return w;
      }
    }
  }
  
  
  private static class Shell extends Fun {
    @Override public String repr() {
      return "⎕SH";
    }
    
    @Override
    public Value call(Value w) {
      return exec(w, null, null, false);
    }
    
    @Override
    public Value call(Value a, Value w) {
      APLMap m = (APLMap) a;
      
      File dir = null;
      Obj diro = m.getRaw("dir");
      if (diro != Null.NULL) dir = new File(((Value) diro).asString());
      
      byte[] inp = null;
      Obj inpo = m.getRaw("inp");
      if (inpo != Null.NULL) {
        Value inpv = (Value) inpo;
        if (inpv.ia > 0) {
          if (inpv.first() instanceof Char) inp = inpv.asString().getBytes(StandardCharsets.UTF_8);
          else {
            inp = new byte[inpv.ia];
            double[] ds = inpv.asDoubleArr();
            for (int i = 0; i < ds.length; i++) inp[i] = (byte) ds[i];
          }
        }
      }
      
      boolean raw = false;
      Obj rawo = m.getRaw("raw");
      if (rawo != Null.NULL) raw = Main.bool(rawo);
      
      return exec(w, dir, inp, raw);
    }
    
    public Value exec(Value w, File f, byte[] inp, boolean raw) {
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
        if (inp != null) p.getOutputStream().write(inp);
        p.getOutputStream().close();
        byte[] out = readAllBytes(p.getInputStream());
        byte[] err = readAllBytes(p.getErrorStream());
        Num ret = Num.of(p.waitFor());
        if (raw) return new HArr(new Value[]{ret, new DoubleArr(out), new DoubleArr(err)});
        else return new HArr(new Value[]{ret, Main.toAPL(new String(out, StandardCharsets.UTF_8)),
                                              Main.toAPL(new String(err, StandardCharsets.UTF_8))});
      } catch (Throwable e) {
        e.printStackTrace();
        return Null.NULL;
      }
    }
    private byte[] readAllBytes(InputStream is) {
      try {
        byte[] res = new byte[512];
        int used = 0;
        read: while (true) {
          while (used < res.length) {
            int n = is.read(res, used, res.length-used);
            if (n==-1) break read;
            used+= n;
          }
          if (used==res.length) res = Arrays.copyOf(res, res.length*2);
        }
        return Arrays.copyOf(res, used);
      } catch (IOException e) {
        throw new DomainError("failed to read I/O", this);
      }
    }
  }
  
  
  private class NC extends Fun {
    @Override public String repr() {
      return "⎕NC";
    }
    
    @Override public Value call(Value w) {
      Obj obj = get(w.asString());
      if (obj == null) return Num.ZERO;
      if (obj instanceof Value) return Num.NUMS[2];
      if (obj instanceof Fun  ) return Num.NUMS[3];
      if (obj instanceof Dop  ) return Num.NUMS[4];
      if (obj instanceof Mop  ) return Num.NUMS[5];
      return Num.NUMS[9];
    }
  }
  
  
  private static class Hasher extends Builtin {
    @Override public String repr() {
      return "⎕HASH";
    }
    @Override public Value call(Value w) {
      return Num.of(w.hashCode());
    }
  }
  private static class Stdin extends Builtin {
    @Override public String repr() {
      return "⎕STDIN";
    }
    @Override public Value call(Value w) {
      if (w instanceof Num) {
        int n = w.asInt();
        ArrayList<Value> res = new ArrayList<>(n);
        for (int i = 0; i < n; i++) res.add(Main.toAPL(Main.console.nextLine()));
        return new HArr(res);
      }
      if (w.ia == 0) {
        ArrayList<Value> res = new ArrayList<>();
        while (Main.console.hasNext()) res.add(Main.toAPL(Main.console.nextLine()));
        return new HArr(res);
      }
      throw new DomainError("⎕STDIN needs either ⍬ or a number as ⍵", this);
    }
  }
  
  private static class Profiler extends Builtin {
    Profiler(Scope sc) {
      super(sc);
    }
    
    static final HashMap<String, Pr> pfRes = new HashMap<>();
    static Obj results() {
      Value[] arr = new Value[pfRes.size()*4+4];
      arr[0] = new ChrArr("expr");
      arr[1] = new ChrArr("calls");
      arr[2] = new ChrArr("total ms");
      arr[3] = new ChrArr("avg ms");
      final int[] p = {4};
      pfRes.forEach((s, pr) -> {
        arr[p[0]++] = Main.toAPL(s);
        arr[p[0]++] = new Num(pr.am);
        arr[p[0]++] = new Num(Math.floor(pr.ms*1e6      )/1e6);
        arr[p[0]++] = new Num(Math.floor(pr.ms*1e6/pr.am)/1e6);
      });
      pfRes.clear();
      return new HArr(arr, new int[]{arr.length>>2, 4});
    }
    
    @Override public String repr() {
      return "⎕PFX";
    }
    @Override public Value call(Value w) {
      return call(w, w);
    }
    public Value call(Value a, Value w) {
      Obj o = callObj(a, w);
      if (o instanceof Value) return (Value) o;
      throw new DomainError("Was expected to give array, got "+o.humanType(true), this);
    }
    
    private static Pr get(Value a, Value w) {
      String s = w.asString();
      String k = a.asString();
      Pr p = pfRes.get(k);
      if (p == null) pfRes.put(k, p = new Pr(Tokenizer.tokenize(s)));
      p.am++;
      return p;
    }
    
    public Obj callObj(Value a, Value w) {
      Pr p = get(a, w);
      BasicLines t = p.tok;
      long sns = System.nanoTime();
      Obj res = Main.execLines(t, sc);
      long ens = System.nanoTime();
      p.ms+= (ens-sns)/1000000d;
      return res;
    }
    
    static class ProfilerOp extends Mop {
      
      public ProfilerOp(Scope sc) {
        super(sc);
      }
      
      Pr get(Obj f) {
        String s = ((Value) f).asString();
        Pr p = pfRes.get(s);
        if (p == null) {
          pfRes.put(s, p = new Pr(Tokenizer.tokenize(s)));
          p.fn = (Fun) Main.execLines(p.tok, sc);
        }
        p.am++;
        return p;
      }
      
      public Value call(Obj f, Value w, DerivedMop derv) {
        Pr p = get(f);
        
        long sns = System.nanoTime();
        Value r = p.fn.call(w);
        long ens = System.nanoTime();
        p.ms+= (ens-sns)/1000000d;
        return r;
      }
      
      public Value call(Obj f, Value a, Value w, DerivedMop derv) {
        Pr p = get(f);
        
        long sns = System.nanoTime();
        Value r = p.fn.call(a, w);
        long ens = System.nanoTime();
        p.ms+= (ens-sns)/1000000d;
        return r;
      }
      
      public String repr() {
        return "⎕PFO";
      }
    }
  }
  
  private static class Pr {
    private final BasicLines tok;
    private int am;
    private double ms;
    private Fun fn;
    
    public Pr(BasicLines tok) {
      this.tok = tok;
    }
  }
  
  private static class Big extends Fun {
    @Override public Value call(Value w) {
      return rec(w);
    }
    private Value rec(Value w) {
      if (w instanceof Num) return new BigValue(((Num) w).num);
      if (w instanceof Primitive) return w;
      Value[] pa = w.values();
      Value[] va = new Value[pa.length];
      for (int i = 0; i < pa.length; i++) {
        va[i] = rec(pa[i]);
      }
      return new HArr(va, w.shape);
    }
    
    @Override public Value callInv(Value w) {
      return recN(w);
    }
    private Value recN(Value w) {
      if (w instanceof BigValue) return ((BigValue) w).num();
      if (w instanceof Primitive) return w;
      if (w instanceof DoubleArr) return w;
      Value[] pa = w.values();
      Value[] va = new Value[pa.length];
      for (int i = 0; i < pa.length; i++) {
        va[i] = recN(pa[i]);
      }
      return Arr.create(va, w.shape);
    }
    @Override public String repr() {
      return "⎕BIG";
    }
  }
  
  private static class DR extends Fun {
    /*
       0=100| - unknown
       1=100| - bit
       2=100| - char
       3=100| - 64-bit float
       4=100| - map
       5=100| - bigint
       6=100| - `fn
       9=100| - null
      
      0=÷∘100 - primitive
      1=÷∘100 - array
    */
    public Value call(Value w) {
      if (w instanceof    BitArr) return Num.of(101);
      if (w instanceof      Char) return Num.of(  2);
      if (w instanceof    ChrArr) return Num.of(102);
      if (w instanceof       Num) return Num.of(  3);
      if (w instanceof DoubleArr) return Num.of(103);
      if (w instanceof    APLMap) return Num.of(  4);
      if (w instanceof  BigValue) return Num.of(  5);
      if (w instanceof    ArrFun) return Num.of(  8);
      if (w instanceof      Null) return Num.of(  9);
      if (w instanceof       Arr) return Num.of(100);
      if (w instanceof Primitive) return Num.of(  0);
      return Num.of(200); // idk ¯\_(ツ)_/¯
    }
    public Value call(Value a, Value w) {
      int[] is = a.asIntVec();
      if (is.length != 2) throw new DomainError("⎕DR expected ⍺ to have 2 items", this);
      int f = is[0];
      int t = is[1];
      if ((f==1 || f==3 || f==5)
       && (t==1 || t==3 || t==5)
       && (f==3 ^ t==3)) { // convert float to/from bits/long
        // if (w instanceof Num) return new BigValue(Double.doubleToLongBits(w.asDouble()), false);
        // return new Num(Double.longBitsToDouble(((BigValue) w).i.longValue()));
        if (t==3) {
          if (f==1) return OverBuiltin.on(this, new Fun() {
            public String repr() { return ""; }
            public Value call(Value w) {
              return new Num(Double.longBitsToDouble(((BigValue) UTackBuiltin.on(BigValue.TWO, w, DR.this)).longValue()));
            }
          }, 1, w);
          if (f==5) return OverBuiltin.on(this, new Fun() {
            public String repr() { return ""; }
            public Value call(Value w) {
              return new Num(Double.longBitsToDouble(((BigValue) w).longValue()));
            }
          }, 0, w);
        } else {
          if (t==1) return OverBuiltin.on(this, new Fun() {
            public String repr() { return ""; }
            public Value call(Value w) {
              return new BitArr(new long[]{Long.reverse(Double.doubleToRawLongBits(w.asDouble()))}, new int[]{64});
            }
          }, 0, w);
          if (t==5) return OverBuiltin.on(this, new Fun() {
            public String repr() { return ""; }
            public Value call(Value w) {
              return new BigValue(Double.doubleToRawLongBits(w.asDouble()));
            }
          }, 0, w);
        }
      }
      throw new NYIError(a+"⎕DR not implemented", this);
    }
    public Value callInvW(Value a, Value w) {
      return call(ReverseBuiltin.on(a), w);
    }
    public String repr() {
      return "⎕DR";
    }
  }
  
}