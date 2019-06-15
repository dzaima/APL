Interpreter it = new DzaimaAPL(); // current interpreter


abstract class Interpreter {
  abstract String[] get(String code);
  abstract String[] special(String ex);
}
class Dyalog extends Interpreter {
  String[] get(String code) {
    try {
      Scanner s = send("eval", code);
      String ln = s.nextLine();
      return parseJSONArray(ln).getStringArray();
    } catch (Exception e) {
      e.printStackTrace();
      return new String[]{"failed to request:", e.toString()};
    }
  }
  Scanner send(String function, String data) throws Exception {
    URL url = new URL(l + function);
    URLConnection con = url.openConnection();
    HttpURLConnection http = (HttpURLConnection)con;
    http.setRequestMethod("POST");
    http.setDoOutput(true);
    StringBuilder b = new StringBuilder("\"");

    for (char c : data.toCharArray()) {
      if (c >= 128) b.append("\\u").append(String.format("%04X", (int) c));
      else if (c == '"') b.append("\\\"");
      else if (c == '\\') b.append("\\\\");
      else b.append(c);
    }
    b.append("\"");
    byte[] bytes = b.toString().getBytes(StandardCharsets.UTF_8);
    http.setFixedLengthStreamingMode(bytes.length);
    http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    http.connect();
    //try (
    OutputStream os = http.getOutputStream();
    //) {
    os.write(bytes);
    //}
    return new Scanner(http.getInputStream());
  }
  String l = "http://localhost:8080/";
  void setLink(String s) {
    l = s;
  }
  String[] special(String s) {
    setLink("http://"+s+"/");
    return new String[0];
  }
}
static Scope dzaimaSC = new Scope();
static Fun layoutUpdate, actionCalled;
static {
  Main.colorful = false;
}

{
  dzaimaSC.set("app", new SimpleMap() {
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
      switch (s) {
        case "layout": return Main.toAPL(kb.data.getString("fullName"));
        case "set": return new Fun() {
          String repr() { return "app.set"; }
          Obj call(Value a, Value w) {
            int[] is = a.asIntVec();
            int x = is[0]; int y = is[1]; int dir = is[2];
            Key key = kb.keys[y][x];
            key.actions[dir] = new Action(parseJSONObject(w.asString()), kb, key);
            return Num.ONE;
          }
        };
        case "graph": return new Fun() {
          String repr() { return "app.graph"; }
          Obj call(Value w) {
            topbar.toNew(new Grapher(w.asString()));
            return Num.ONE;
          }
        };
        case "redraw": redrawAll(); return Num.ONE;
        default: return Null.NULL;
      }
    }
  });
}


class DzaimaAPL extends Interpreter {
  
  Obj eval(String code) {
    try {
      return Main.exec(code, dzaimaSC);
    } catch (Throwable e) {
      return Main.toAPL(e.toString());
    }
  }
  
  String[] get(String code) {
    try {
      Obj v = Main.exec(code, dzaimaSC);
      if (v == null) return new String[0];
      return v.toString().split("\n");
    } catch (APLError e) {
      TPs nSout = new TPs();
      e.print();
      return nSout.end().split("\n");
    } catch (Throwable e) {
      ArrayList<String> lns = new ArrayList();
      lns.add(e + ": " + e.getMessage());
      if (Main.faulty != null && Main.faulty.getToken() != null) {
        String s = repeat(" ", Main.faulty.getToken().spos);
        lns.add(Main.faulty.getToken().raw);
        lns.add(s + "^");
      }
      e.printStackTrace();
      return lns.toArray(new String[0]);
    }
  }
  class TPs extends OutputStream {
    PrintStream oSout;
    TPs() {
      oSout = System.out;
      System.setOut(new PrintStream(this));
    }
    ArrayList<Byte> bs = new ArrayList<Byte>();
    void write(int i) {
      bs.add((byte)(i&0xff));
    }
    String all() {
      byte[] ba = new byte[bs.size()];
      int i = 0;
      for (byte b : bs) {
        ba[i] = b;
        i++;
      }
      return new String(ba);
    }
    String end() {
      System.out.flush();
      System.setOut(oSout);
      return this.all();
    }
  }
  class Ed extends Editor {
    Ed(String name, String val) {
      super(name, val);
    }
    void save(String val) {
      dzaimaSC.set(name, Main.exec(val, dzaimaSC));
    }
  }
  String[] special(String ex) {
    if (ex.toLowerCase().startsWith("ed ")) {
      String nm = ex.substring(3);
      Obj o = dzaimaSC.get(nm);
      if (o instanceof Dfn) {
        topbar.toNew(new Ed(nm, ((Dfn ) o).code.source()));
      }
      if (o instanceof Dmop) {
        topbar.toNew(new Ed(nm, ((Dmop) o).code.source()));
      }
      if (o instanceof Ddop) {
        topbar.toNew(new Ed(nm, ((Ddop) o).code.source()));
      }
      return new String[0];
    }
    TPs nSout = new TPs();
    try {
      Main.ucmd(dzaimaSC, ex);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return nSout.end().split("\n");
  }
}
