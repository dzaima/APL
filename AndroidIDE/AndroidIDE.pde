import java.util.Optional;
ArrayList<Drawable> screen = new ArrayList();
Keyboard kb;
TextReciever textInput;

TextReciever testRec = new TextReciever() {
  String ln = "";
  void append(String str) {
    ln+= str;
    print(str);
  }
  void delete() {
    if (ln.length() == 0) return;
    println("\nignore that");
    ln = ln.substring(0, ln.length()-1);
    print(ln);
  }
  void clear() {
    println("\nignore ALL that...");
    ln = "";
  }
  String allText() {
    return ln;
  }
};
void setup() {
  //size(800, 450);
  background(#0a0a0a);
  textFont(createFont("APL385+.ttf", 48));
  textInput = testRec;
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
  for (Drawable d : screen) {
    d.tick();
  }
  if (redraw) {
    background(#101010);
    kb.redraw();
    redraw = false;
  }
  fill(#101010);
  rect(0, 0, width, 100);
  textAlign(LEFT, TOP);
  fill(#D2D2D2);
  textSize(min(width, height)/20);
  text(testRec.allText(), 0, 0);
  pmousePressed = mousePressed;
}
void keyPressed() {
  resize(height, width);
}
