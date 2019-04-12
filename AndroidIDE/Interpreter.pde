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
    //try {
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
  String l = "http://192.168.1.103:8080/"; // "http://localhost:8080/";
  void setLink(String s) {
    l = s;
  }
  String[] special(String s) {
    setLink(s);
    return new String[0];
  }
}
Scope dzaimaSC = new Scope();
static {
  Main.colorful = false;
}
class DzaimaAPL extends Interpreter {
  String[] get(String code) {
    try {
      Obj v = Main.exec(code, dzaimaSC);
      return v.toString().split("\n");
    } catch (APLError e) {
      TPs nSout = new TPs();
      e.print();
      return nSout.end().split("\n");
    } catch (Throwable e) {
      ArrayList<String> lns = new ArrayList();
      lns.add(e + ": " + e.getMessage());
      if (Main.faulty != null && Main.faulty.getToken() != null) {
        String s = repeat(" ", Main.faulty.getToken().pos);
        lns.add(Main.faulty.getToken().line);
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
  String[] special(String ex) {
    TPs nSout = new TPs();
    try {
      Main.ucmd(dzaimaSC, ex);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return nSout.end().split("\n");
  }
}
