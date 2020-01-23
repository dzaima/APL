import java.text.DecimalFormat;
import java.io.PrintStream;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;


static PGraphics d;
static AndroidIDE a;


static ArrayList<Drawable> screen;

static TextReciever textInput;
static Keyboard kb;

static TopBar topbar;
static int top = 30;
static int isz = 30;
static int freey() { // y position of keyboard start
  return a.height-kb.h;
}
static StrOS os;
static REPL mainREPL;



void setup() {
  d = g;
  a = this;
  if (screen==null) screen = new ArrayList();
  background(#0a0a0a);
  int max = max(width, height);
  top = isz = max/40;
  
  
  newKb();
  if (topbar==null) { // don't reset variables if orientation has changed
    os = new StrOS();
    topbar = new TopBar(0, 0, width, top);
    topbar.toNew(mainREPL = new REPL());
    topbar.show();
  }
  textFont(createFont("APL385+.ttf", 48));
  redrawAll();
}
static boolean redraw;
void newKb() {
  if (width>height) keyboard(0, 0, width, width/3, "L.json");
  else              keyboard(0, 0, width, (int)(width*.8), "P.json");
}
static void redrawAll() {
  //if (width != w || h != height) surface.setSize(w, h);
  redraw = true;
  a.newKb();
  topbar.resize(d.width, top);
}
static boolean pmousePressed;
static int smouseX, smouseY;
static int mouseStart;
void draw() {
  psDraw();
  if (!pmousePressed && mousePressed) {
    smouseX = mouseX;
    smouseY = mouseY;
    mouseStart = millis();
  }
  // if (mouseButton == RIGHT) redrawAll();
  for (int i = screen.size()-1; i >= 0; i--) {
    screen.get(i).tick();
  }
  if (redraw) {
    background(#101010);
    for (Drawable d : screen) {
      d.redraw();
    }
    redraw = false;
  }
  String s = os.get();
  if (s.length() != 0) {
    if (mainREPL != null) mainREPL.historyView.appendLns(s);
  }
  pmousePressed = mousePressed;
}
static boolean shift, ctrl;
void keyPressed(KeyEvent e) {
  //if (key == 'Q') {
  //  surface.setSize(height, width);
  //  redrawAll();
  //}
  e = fixKE(e);
  //println(+key, keyCode, shift, ctrl, e.isAltDown(), e.isMetaDown());
  shift = e.isShiftDown();
  ctrl  = e.isControlDown();
  //println(e.getNative());
  if (key == 18 && keyCode == 82) {
    redrawAll();
    return;
  }
  if (textInput != null) {
    if (key == 65535) {
      handleCoded(keyCode);
    } else {
      if (key == 8) textInput.ldelete();
      else if (key ==  26 && keyCode ==  90) textInput.special("undo");
      else if (key ==  25 && keyCode ==  89) textInput.special("redo");
      else if (key ==   3 && keyCode ==  67) textInput.special("copy");
      else if (key ==  22 && keyCode ==  86) textInput.special("paste");
      else if (key ==  24 && keyCode ==  88) textInput.special("cut");
      else if (key == 127 && keyCode == 127) textInput.rdelete();
      else if (key ==  19 && keyCode ==  83) textInput.special("eval");
      else if (key ==   1 && keyCode ==  65) textInput.special("sall");
      else textInput.append(Character.toString(key));
    }
  }
  //println(+key, keyCode);
}
void keyReleased(KeyEvent e) {
  shift = e.isShiftDown();
}

static boolean shift() {
  return shift || (textInput!=null? kb.shiftMode>0 : false);
}
static boolean cshift() {
  boolean r = shift || (kb!=null? kb.shiftMode>0 : false);
  if (kb!=null && kb.shiftMode>0) kb.shiftMode = 2;
  return r;
}
static void textS(PGraphics g, String s, float x, float y) {
  g.text(s, x, y + (MOBILE? g.textSize*.333 : 0));
}
static void textS(PGraphics g, char s, float x, float y) {
  g.text(s, x, y + (MOBILE? g.textSize*.333 : 0));
}
