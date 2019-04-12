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
Keyboard kb;
TextReciever textInput;
ROText REPLH;
void setup() {
  //size(800, 450);
  //size(450, 800);
  //size(550, 560);
  size(540, 830);
  //fullScreen();
  background(#0a0a0a);
  textFont(createFont("APL385+.ttf", 48));
  REPLH = new ROText(0, 0, width, 340);
  APLField f = new APLField(0, 350, width, 40) {
    Interpreter it = new Dyalog();
    
    void eval() {
      textln("  "+line+"\n");
      if (line.startsWith(":")) {
        String cmd = line.substring(1);
        int i = cmd.indexOf(" "); 
        String nm = i==-1? cmd : cmd.substring(0, i);
        String arg = i==-1? "" : cmd.substring(i+1);
        String argl = arg.toLowerCase();
        if (nm.equals("sz")) REPLH.setSize(int(arg));
        else if (nm.equals("i")) {
          if (argl.equals("dyalog")) {
            it = new Dyalog();
          }
          if (argl.equals("dzaima")) {
            it = new DzaimaAPL();
          }
        } else if (nm.equals("clear")) {
          REPLH.set(new ArrayList());
        } else textln("Command "+nm+" not found");
        //else if (nm.equals(""))
        return;
      }
      
      if (line.startsWith(")")) {
        for (String s : it.special(line.substring(1))) textln(s);
        return;
      }
      String[] res = it.get(line);
      for (String ln : res) {
        textln(ln);
      }
    }
    void textln(String ln) {
      REPLH.append(ln);
    }
    void newline() {
      eval();
      clear();
    }
  };
  textInput = f;
  resize(width, height);
}
boolean redraw;
//@Override void orientation() {
  
//}
void resize(int w, int h) {
  //if (width != w || h != height) surface.setSize(w, h);
  redraw = true;
  if (width>height) keyboard(0, 0, width, width/3, "L.json");
  else              keyboard(0, 0, width, (int)(width*.8), "P.json");
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
  if (mouseButton == RIGHT) resize(width, height);
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
void keyPressed() {
  if (key == 65535) {
         if (keyCode == 38) textInput.special("up");
    else if (keyCode == 37) textInput.special("left");
    else if (keyCode == 40) textInput.special("down");
    else if (keyCode == 39) textInput.special("right");
  } else {
    if (key == 8) textInput.backspace();
    else if (key == 26 && keyCode == 90) textInput.special("undo");
    else if (key == 25 && keyCode == 89) textInput.special("redo");
    else textInput.append(Character.toString(key));
  }
  //println(+key, keyCode);
  //resize(height, width);
}
