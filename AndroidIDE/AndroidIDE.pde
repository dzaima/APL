import java.awt.Toolkit;
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
int freey;
void setup() {
  background(#0a0a0a);
  textFont(createFont("APL385+.ttf", 48));
  topbar = new TopBar(0, 0, width, top);
  topbar.toNew(new REPL());
  topbar.show();
  redrawAll();
}
boolean redraw;
void redrawAll() {
  //if (width != w || h != height) surface.setSize(w, h);
  redraw = true;
  if (width>height) keyboard(0, 0, width, width/3, "L.json");
  else              keyboard(0, 0, width, (int)(width*.8), "P.json");
  freey = height-kb.h;
  topbar.resize(width, top);
  topbar.resized();
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
  for (Drawable d : screen) {
    d.tick();
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
