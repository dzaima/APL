void exec() {
  try {
    resVal = APL.Main.exec(program.replace("α", "⍺").replace("ω", "⍵"), global);
    res = resVal.toString();
  } catch (Throwable e) {
    System.err.println("Error executing APL:");
    e.printStackTrace();
    res = e.getMessage();
    if (res == null) res = e.toString();
  }
  resSize = th;
  textSize(resSize);
  while (textWidth(res) > 5*w) {
    textSize(--resSize);
    if (resSize < 0) {
      resSize = th;
      break;
    }
  }
}
void thr() {
  new Error().printStackTrace();
  delay(3000);
  throw null;
}
