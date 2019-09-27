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
ArrayList<Drawable> screen = new ArrayList();
TextReciever textInput;

Keyboard kb;

TopBar topbar;
int top = 30;
int isz = 30;
int freey() { // y position of keyboard start
  return height-kb.h;
}
void setup() {
  background(#0a0a0a);
  int max = max(width, height);
  top = isz = max/40;
  textFont(createFont("APL385+.ttf", 48));
  newKb();
  topbar = new TopBar(0, 0, width, top);
  topbar.toNew(new REPL());
  topbar.show();
  redrawAll();
}
boolean redraw;
void newKb() {
  if (width>height) keyboard(0, 0, width, width/3, "L.json");
  else              keyboard(0, 0, width, (int)(width*.8), "P.json");
}
void redrawAll() {
  //if (width != w || h != height) surface.setSize(w, h);
  redraw = true;
  newKb();
  topbar.resize(width, top);
}
boolean pmousePressed;
int smouseX, smouseY;
int mouseStart;
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
  pmousePressed = mousePressed;
}
boolean shift;
void keyPressed(KeyEvent e) {
  shift = e.isShiftDown();
  if (key == 18 && keyCode == 82) {
    redrawAll();
    return;
  }
  if (textInput != null) {
    if (key == 65535) {
           if (keyCode == 38) textInput.special("up");
      else if (keyCode == 37) textInput.special("left");
      else if (keyCode == 40) textInput.special("down");
      else if (keyCode == 39) textInput.special("right");
    } else {
      if (key == 8) textInput.ldelete();
      else if (key ==  26 && keyCode ==  90) textInput.special("undo");
      else if (key ==  25 && keyCode ==  89) textInput.special("redo");
      else if (key ==   3 && keyCode ==  67) textInput.special("copy");
      else if (key ==  22 && keyCode ==  86) textInput.special("paste");
      else if (key == 127 && keyCode == 127) textInput.rdelete();
      else if (key ==  19 && keyCode ==  83) textInput.special("eval");
      else textInput.append(Character.toString(key));
    }
  }
  //println(+key, keyCode);
}
void keyReleased(KeyEvent e) {
  shift = e.isShiftDown();
}

boolean shift() {
  return shift || (textInput!=null? kb.shiftMode>0 : false);
}
boolean cshift() {
  boolean r = shift || (kb!=null? kb.shiftMode>0 : false);
  if (kb!=null && kb.shiftMode>0) kb.shiftMode = 2;
  return r;
}
void textS(String s, float x, float y) {
  textS(g, s, x, y);
}
static void textS(PGraphics g, String s, float x, float y) {
  g.text(s, x, y + (MOBILE? g.textSize*.333 : 0));
}
static void textS(PGraphics g, char s, float x, float y) {
  g.text(s, x, y + (MOBILE? g.textSize*.333 : 0));
}
