abstract class Interpreter {
  abstract String[] get(String code);
  abstract void special(String ex);
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
  void special(String s) {
    setLink(s);
  }
}
Scope dzaimaSC = new Scope();
class DzaimaAPL extends Interpreter {
  String[] get(String code) {
    Obj v = Main.exec(code, dzaimaSC);
    return v.toString().split("\n");
  }
  void special(String ex) {
    
  }
}
