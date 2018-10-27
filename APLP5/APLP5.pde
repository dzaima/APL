static final Null NULL = Null.NULL;
Scope global;
APLP5 thisobj = this;
boolean inSettings = false;
APLGraphics mainGraphics;
P5 p5;
void settings() {
  Main.colorful = false;
  mainGraphics = new APLGraphics();
  inSettings = true;
  if (args == null) args = new String[]{"draw"};
  
  global = new Scope();
  String s = new String(loadBytes(args[0]));
  p5 = new P5();
  global.set("P5", p5);
  p5.lm = new MouseButton(LEFT);
  p5.rm = new MouseButton(RIGHT);
  p5.mm = new MouseButton(CENTER);
  //Main.debug = true;
  exec(s);
}
void setup() {
  mainGraphics.g = g;
  inSettings = false;
  call(setup, arr(mouseX, mouseY));
}
void exec(String s) {
  try {
    APL.Main.exec(s, global);
  } catch (Throwable e) {
    e.printStackTrace();
    throw e;
  }
}
void call(Fun f, Value a, Value w) {
  if (f == null) return;
  try {
    f.call(a, w);
  } catch (APLError e) {
    e.print();
    e.printStackTrace();
    throw e;
  } catch (Throwable e) {
    e.printStackTrace();
    throw e;
  }
}
void call(Fun f, Value w) {
  if (f == null) return;
  try {
    f.call(w);
  } catch (APLError e) {
    e.print();
    e.printStackTrace();
    throw e;
  } catch (Throwable e) {
    e.printStackTrace();
    throw e;
  }
}
