static DzaimaAPL mainIt;
static REPL mainREPL;
static Sys mainSys;
static Fun layoutUpdate, actionCalled;
static AppMap appobj;
static REPL mainInit() {
  Main.colorful = false;
  
  mainIt = new DzaimaAPL(); // current interpreter
  mainREPL = new REPL("REPL", mainIt);
  mainSys = mainIt.sys;
  
  appobj = new AppMap();
  mainSys.gsc.set("app", appobj);
  
  return mainREPL;
}


abstract static class Interpreter {
  ItListener l;
  
  abstract Theme theme();
  abstract void sendLn(String ln);
  abstract void tick();
  abstract void intJSON(JSONArray a);
  
  abstract Fun getFn(String ln);
  abstract void close();
}
abstract static class ItListener {
  abstract void println(String ln);
  abstract void print(String ln);
  abstract void off(int code);
  abstract String input();
  abstract void inputMode(boolean enabled, boolean highlight);
  abstract void guiJSON(JSONArray a);
  
}

static RIDE latestRIDE;
import java.net.*;
import java.io.*;
void startRIDE() {
  RIDE r = latestRIDE;
  try {
    //Process dyp = Runtime.getRuntime().exec(new String[]{"dyalog", "+s", "-q", "-nokbd"}, new String[]{"RIDE_INIT=CONNECT:"+r.ip, "RIDE_SPAWNED=1"});
    ServerSocket ss = new ServerSocket(Integer.parseInt(split(r.ip,':')[1]));
    while(true) {
      Socket s = ss.accept();
      //Socket s = new Socket("127.0.0.1",8000);
      DataInputStream i = new DataInputStream(s.getInputStream());
      TW o = new TW(s.getOutputStream());
      o.send("SupportedProtocols=2");
      o.send("UsingProtocol=2");
      o.send("[\"Identify\",{\"identity\":1}]");
      o.send("[\"Connect\",{\"remoteId\":2}]");
      o.send("[\"GetWindowLayout\",{}]");
      r.o = o;
      int num = 0;
      while(true) {
        while(i.available()==0) {
          delay(500);
          if (r.closed) {
            i.close();
            s.close();
            ss.close();
            return;
          }
        }
        int len = 0;
        for (int j = 0; j < 4; j++) len = len*256 + (i.read()&0xff);
        len-= 8;
        for (int j = 0; j < 4; j++) i.read();
        byte[] bs = new byte[len];
        int p=0;while(p!=bs.length) p+= i.read(bs, p, bs.length-p);
        String msg = new String(bs);
        if (msg.length()>400) println("received "+len+" "+msg.substring(0,400)+"…");
        else println("received "+msg);
        if (num>=2) r.recv.add(parseJSONArray(msg));
        num++;
      }
    }
  } catch(IOException e) {
    e.printStackTrace();
  }
}
static class TW {
  OutputStream o;
  TW(OutputStream o) {
    this.o = o;
  }
  void send(JSONArray a) {
    send(a.format(-1));
  }
  void send(String s) {
    try {
      println("sending "+s);
      byte[] bs = s.getBytes(StandardCharsets.UTF_8);
      byte[] n = new byte[bs.length+8];
      n[3]=(byte)(n.length    &0xff);
      n[2]=(byte)(n.length>>8 &0xff);
      n[1]=(byte)(n.length>>16&0xff);
      n[0]=(byte)(n.length>>24&0xff);
      System.arraycopy("RIDE".getBytes(),0,n,4,4);
      System.arraycopy(bs,0,n,8,bs.length);
      o.write(n);
      o.flush();
    } catch(Throwable t) {
      t.printStackTrace();
    }
  }
}


static class RIDE extends Interpreter {
  ConcurrentLinkedQueue<JSONArray> recv = new ConcurrentLinkedQueue();
  TW o;
  String ip;
  boolean closed;
  int promptType;
  RIDE(String ip) {
    this.ip = ip;
    latestRIDE = this;
    a.thread("startRIDE");
    simulateOutput("Connecting to "+ip+"...\n\n");
  }
  
  void simulateOutput(String text) {
      //JSONArray r = new JSONArray();
      //r.setString(0, "AppendSessionOutput");
      //JSONObject o = new JSONObject();
      //o.setString("result", text);
      //r.setJSONObject(1, o);
      recv.add(ja("AppendSessionOutput",jo("result",text)));
  }
  
  void sendLn(String ln) {
    if (o==null) { simulateOutput("Still waiting on connection!\n"); return; }
    try {
      intJSON(ja("Execute",jo("text", (promptType==1?"   ":"")+ln+"\n", "trace", 0)));
    } catch (Exception e) {
      String msg = e.getMessage();
      simulateOutput("Connection error: "+(msg==null? e.getClass().getName() : msg));
    }
  }
  
  HashMap<Integer, EdRIDE> windows = new HashMap();
  
  void tick() {
    int ctr = 0;
    while (!recv.isEmpty() && ctr++<10) {
      JSONArray c = recv.remove();
      String tp = c.getString(0);
      JSONObject v = c.getJSONObject(1);
      if (tp.equals("EchoInput")) {
        l.print(v.getString("input"));
      } else if (tp.equals("ReplyGetLog")) {
        JSONArray r = v.getJSONArray("result");
        for (int i = Math.max(0, r.size()-100); i < r.size(); i++) l.println(r.getString(i));
      } else if (tp.equals("AppendSessionOutput")) {
        l.print(v.getString("result"));
      } else if (tp.equals("OpenWindow")) {
        EdRIDE w = new EdRIDE(v.getString("name"), lnStr(v.getJSONArray("text")), v.getInt("token"), this);
        windows.put(w.id, w);
        topbar.toNew(w);
      } else if (tp.equals("CloseWindow")) {
        topbar.close(windows.get(v.getInt("win")));
      } else if (tp.equals("Disconnect")) {
        l.print("Disconnected.");
      } else if (tp.equals("SetPromptType")) {
        int ty = v.getInt("type");
        promptType = ty;
        l.inputMode(ty!=0, ty==1||ty==2);
      } else if (tp.equals("")) {
        
      }
    }
  }
  
  Fun getFn(String ln) {
    return null;
  }
  
  Theme theme() {
    return noErrTheme;
  }
  
  void intJSON(JSONArray a) {
    o.send(a);
  }
  void close() {
    closed = true;
  }
}
static String lnStr(JSONArray a) {
  StringBuilder b = new StringBuilder();
  for (int i = 0; i < a.size(); i++) {
    if (i!=0) b.append("\n");
    b.append(a.getString(i));
  }
  return b.toString();
}
static class EdRIDE extends Editor {
  int id;
  Interpreter it;
  EdRIDE(String name, String val, int id, Interpreter it) {
    super(name, val);
    this.id = id;
    this.it = it;
  }
  void save(String val) {
    it.intJSON(ja("SaveChanges", jo("win", id, "text", split(val, '\n'))));
  }
  void close() {
    it.intJSON(ja("CloseWindow",jo("win", id)));
  }
}

public static byte[] readAll(InputStream s) throws IOException {
  byte[] b = new byte[1024];
  int i = 0, am;
  while ((am = s.read(b, i, b.length-i))!=-1) {
    i+= am;
    if (i==b.length) b = Arrays.copyOf(b, b.length*2);
  }
  return Arrays.copyOf(b, i);
}
static class TryAPL extends Interpreter {
  static class SentThing { String line; String print; SentThing(String line, String print) { this.line=line; this.print=print; } }
  ConcurrentLinkedQueue<String[]> recv = new ConcurrentLinkedQueue();
  ConcurrentLinkedQueue<SentThing> send = new ConcurrentLinkedQueue();
  boolean closed;
  AtomicBoolean running = new AtomicBoolean(false);
  TryAPL() {
    (new Thread() {
      String state0 = "";
      int    state1 = 0;
      String state2 = "";
      void run() {
        while(true) {
          running.set(!send.isEmpty());
          SentThing expr;
          do {
            a.delay(100);
            if (closed) { println("[TryAPL] Closing thread"); return; }
            expr = send.poll();
          } while (expr==null);
          running.set(true);
          println("[TryAPL] Sending "+expr.line);
          String s = ja(state0,state1,state2,expr.line).format(-1);
          byte[] data = s.getBytes(StandardCharsets.UTF_8);
          try {
            URL u = new URL("https://tryapl.org/Exec");
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setUseCaches(false);
            c.setRequestProperty("Content-Type", "application/json"); //"application/x-www-form-urlencoded");
            c.setRequestProperty("Content-Length", Integer.toString(data.length));
            c.setRequestProperty("Content-Language", "en-US");
            
            c.setDoOutput(true);
            OutputStream os = c.getOutputStream();
            os.write(data);
            os.close();
            InputStream is = c.getInputStream();
            JSONArray arr = a.parseJSONArray(new String(readAll(is), StandardCharsets.UTF_8));
            is.close();
            println("[TryAPL] Received answer");
            state0 = arr.getString(0);
            state1 = arr.getInt(1);
            state2 = arr.getString(2);
            JSONArray lns = arr.getJSONArray(3);
            if (lns.size()==1 && lns.getString(0).startsWith("\b")) {
              String[] parts = split(lns.getString(0),'\b');
              if (toURL(parts[2])) lns = new JSONArray();
              else lns.setString(0, parts[parts.length-1]);
            }
            int off = expr.print!=null?1:0;
            String[] lnsArr = new String[lns.size()+off];
            if (expr.print!=null) lnsArr[0] = expr.print;
            for (int i = 0; i < lns.size(); i++) lnsArr[i+off] = lns.getString(i);
            recv.add(lnsArr);
          } catch (Exception e) {
            recv.add(new String[]{e.getMessage()==null? e.getClass().getName() : e.getMessage()});
          }
        }
      }
    }).start();
  }
  void sendLn(String ln) {
    String print = "   "+ln;
    if (running.getAndSet(true)) {
      send.add(new SentThing(ln, print));
    } else {
      l.println(print);
      send.add(new SentThing(ln, null));
    }
  }
  boolean prevRunning;
  void tick() {
    while (!recv.isEmpty()) {
      String[] lns = recv.poll();
      for (String s : lns) l.println(s);
    }
    boolean currRunning = running.get();
    if (currRunning!=prevRunning) {
      l.inputMode(!currRunning, true);
      prevRunning = currRunning;
    }
  }
  Theme theme() { return aplTheme; }
  void intJSON(JSONArray o) { }
  Fun getFn(String ln) { return null; }
  void close() { closed = true; }
}


static class DzaimaAPL extends Interpreter {
  public final Sys sys = new Sys() {
    public void println(String s) { l.println(s); }
    public void print(String s) { l.print(s); }
    public void colorprint(String s, int col) { l.println(s); }
    public void off(int code) { l.off(code); }
    
    public String input() { return l.input(); }
  };
  
  void sendLn(String s) {
    l.println("  "+s);
    if (s.startsWith(")")) {
      String[] parts = split(s,' ');
      String type = parts[0].substring(1);
      String arg = parts.length==1? "" : s.substring(type.length()+2);
      if (type.equals("ed")) {
        if (parts.length!=2) { l.println(")ed: Expected an argument object name"); return; }
        Obj o = sys.csc.get(parts[1]);
        if      (o instanceof  Dfn) { topbar.toNew(new Ed(sys.csc, parts[1], ((Dfn ) o).code.source())); }
        else if (o instanceof Dmop) { topbar.toNew(new Ed(sys.csc, parts[1], ((Dmop) o).code.source())); }
        else if (o instanceof Ddop) { topbar.toNew(new Ed(sys.csc, parts[1], ((Ddop) o).code.source())); }
        else if (o==null) l.println(")ed: Variable \""+parts[1]+"\" doesn't exist");
        else l.println(")ed: Can only edit dfns and dops");
        return;
      }
      if (type.equals("ef") || type.equals("efx")) {
        boolean ex = type.equals("efx");
        if (parts.length==1) { l.println(")"+type+": Expected an argument"); return; }
        String[] ps = arg.split("/");
        String[] lns = a.loadStrings(arg);
        Scope sc = sys.csc;
        topbar.toNew(new Editor(ps[ps.length-1], lns==null? "" : join(lns, "\n")) {
          public void save(String t) {
            try {
              a.saveStrings(arg, new String[]{t});
              if (ex) Main.exec(ta.allText(), sc);
            } catch (Throwable e) {
              sys.report(e);
            }
          }
        });
        return;
      }
      if (type.equals("fx")) {
        if (parts.length==1) { l.println(")"+type+": Expected an argument"); return; }
        String[] lns = a.loadStrings(arg);
        if (lns==null) { l.println(")fx: Failed to read file"); return; }
        try {
          Main.exec(join(lns, "\n"), sys.gsc);
        } catch (Throwable e) {
          sys.report(e);
        }
        return;
      }
    }
    sys.lineCatch(s);
  }
  
  
  Fun getFn(String ln) {
    return (Fun) Main.exec(ln, sys.csc);
  }
  Theme theme() { return aplTheme; }
  void tick() { }
  void intJSON(JSONArray o) { }
  void close() { }
}
static class Ed extends Editor {
  Scope sc;
  Ed(Scope sc, String name, String val) {
    super(name, val);
    this.sc = sc;
  }
  void save(String val) {
    try {
      sc.set(name, Main.exec(val, sc));
    } catch (Throwable t) {
      sc.sys.report(t);
    }
  }
}















static class AppMap extends SimpleMap {
  String toString() { return "app"; }
  
  void setv(String k, Obj v) {
    String s = k.toLowerCase();
    switch (s) {
      case "update":
        layoutUpdate = (Fun) v;
        layoutUpdate.call(Main.toAPL(kb.data.getString("fullName")), Main.toAPL(kb.layout));
        kb.redraw();
      return;
      case "action": actionCalled = (Fun) v; return;
      default: throw new DomainError("setting non-existing key "+s+" for app");
    }
  }
  Obj getv(String k) {
    String s = k.toLowerCase();
    if (s.matches("t\\d+")) {
      int i = Integer.parseInt(s.substring(1)) - mainSys.csc.IO;
      if (i < topbar.tabs.size()) return topbar.tabs.get(i);
    }
    switch (s) {
      case "layout": return Main.toAPL(kb.data.getString("fullName"));
      case "set": return new Fun() {
        public String repr() { return "app.set"; }
        public Value call(Value a, Value w) {
          int[] is = a.asIntVec();
          int x = is[0]; int y = is[1]; int dir = is[2];
          Key key = kb.keys[y][x];
          key.actions[dir] = new Action(AndroidIDE.a.parseJSONObject(w.asString()), kb, key);
          return Num.ONE;
        }
      };
      case "graph": return new Fun() {
        public String repr() { return "app.graph"; }
        public Value call(Value w) {
          Grapher g = new Grapher(mainIt, w.asString());
          topbar.toNew(g);
          return g;
        }
      };
      case "cpy": return new Fun() {
        public String repr() { return "app.cpy"; }
        public Value call(Value w) {
          if (w.rank == 1) {
            w = w.squeeze();
            if (w instanceof ChrArr) {
              copy(w.asString());
              return Num.ONE;
            }
          }
          copy(w.toString());
          return Num.ONE;
        }
      };
      case "redraw": redrawAll(); return Num.ONE;
      case "ts": {
        Value[] vs = new Value[topbar.tabs.size()];
        for (int i = 0; i < vs.length; i++) vs[i] = topbar.tabs.get(i);
        return new HArr(vs);
      }
      case "t": return topbar.ctab;
      default: return Null.NULL;
    }
  }
}
