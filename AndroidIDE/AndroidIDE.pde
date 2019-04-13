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
int freey;
void setup() {
  //size(800, 450);
  //size(450, 800);
  //size(550, 560);
  size(540, 830);
  //fullScreen();
  background(#0a0a0a);
  textFont(createFont("APL385+.ttf", 48));
  topbar = new TopBar(0, 0, width, top);
  topbar.toNew(new REPL());
  topbar.add(new REPL());
  topbar.show();
  redrawAll();
}
boolean redraw;
//@Override void orientation() {
  
//}
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
  
  if (!pmousePressed && mousePressed) {
    smouseX = mouseX;
    smouseY = mouseY;
    mouseStart = millis();
  }
  if (mouseButton == RIGHT) redrawAll();
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
  //fill(#101010);
  //rect(0, 0, width, 100);
  //textAlign(LEFT, TOP);
  //fill(#D2D2D2);
  //textSize(min(width, height)/20);
  //text(testRec.allText(), 0, 0);
  pmousePressed = mousePressed;
}
/*
65535 38
65535 37
65535 40
65535 39
*/
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
      else if (key ==  22 && keyCode ==  86) textInput.special("paste");
      else textInput.append(Character.toString(key));
    }
  }
  //println(+key, keyCode);
  //resize(height, width);
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

import java.awt.datatransfer.*;
import java.awt.Toolkit;
void copy(String s) {
  StringSelection stringSelection = new StringSelection(s);
  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  clipboard.setContents(stringSelection, null);
}

String paste() {
  try {
    return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
  } catch (Throwable e) {
    return "";
  }
}
