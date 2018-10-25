import java.util.Arrays;

private static final int[] index = {0x00ffffff, 0xFF134ADB, 0xFF282828, 0xFF353535};

// STATE


private static int w;
private static int hC;
private static int vC;
private static int h;
private static float th;
private static float resSize;

private static Scope global = new Scope();
private static String res = "";
private Obj resVal;
private static String program = "";
private static int ptr = 0;

private static String[] history = new String[300];
private static int[] historyPtrs = new int[300];
private static int histptr = 0;
private static boolean noHist = false;

private static int smouseX, smouseY;
private static boolean pmousePressed;
private static int stouch;
private static int lastTouch = 0;

private int mode; // 0 - calc; 1 - ascii; 2 - graph
private double fullX;
private double fullY;
private double fullS;
private double fullow;

Key[][] chars;
Key[][] Dchars;
Key[][] Echars;

public void setup() {
  setup2();
  textFont(createFont("APL385+.ttf", 48));
  JSONArray def = loadJSONArray("chrs.json");
  hC = def.getJSONArray(0).size();
  vC = def.size();
  Dchars = new Key[vC][hC];
  for (int y = 0; y < vC; y++) {
    JSONArray rowi = def.getJSONArray(y);
    for (int x = 0; x < hC; x++) {
      JSONObject item = rowi.getJSONObject(x);
      Dchars[y][x] = new Key(item, x, y);
    }
  }
  chars = Dchars;
  
  JSONArray extra = loadJSONArray("extra.json");
  Echars = new Key[vC][hC];
  for (int y = 0; y < vC; y++) {
    JSONArray rowi = extra.getJSONArray(y);
    for (int x = 0; x < hC; x++) {
      JSONObject item = rowi.getJSONObject(x);
      Echars[y][x] = new Key(item, x, y);
    }
  }
  
  w = width/hC;
  h = (int) (height/vC*.66);
  th = h * .4f;
  resSize = th;
  APL.Main.exec(
    "sin←{1○⍵×○÷180}\n"+
    "cos←{2○⍵×○÷180}\n"+
    "tan←{3○⍵×○÷180}\n"+
    "asin←{(¯1○⍵)÷○÷180}\n"+
    "acos←{(¯2○⍵)÷○÷180}\n"+
    "atan←{(¯3○⍵)÷○÷180}\n"+
    "\n", global);
}
public synchronized void onBackPressed() {
  if (mode != 0) mode = 0;
  else exit();
}



double lerp(double x, double y, double sc) {
  return x + (y-x)*sc;
}

public void keyPressed(KeyEvent e) {
  //println(fullX, fullY, fullS);
  lastTouch = millis();
  if (key == 27) {
    if (mode != 0) {
      mode = 0;
      key = 0;
    }
  } else if (key == 8) {
    if (ptr == 0) return;
    program = program.substring(0, ptr-1)+program.substring(ptr);
    ptr--;
  }
  else if (key == 10 && e.isControlDown()) exec(); else if (key >= ' ' && key != 65535 || key == 10) ins(key);
}
static Key started;
// change line below to //* for PC mode and /* for android
//*
void setup2() {
  surface.setResizable(true);
}
void mouseWheel(MouseEvent e) {
  double sc = e.getCount()==1? .8 : 1/.8;
  double pS = fullS;
  fullS*= sc;
  double scalechange = 1/fullS - 1/pS;
  fullX-= (mouseX * scalechange);
  fullY-= (mouseY * scalechange);
}
class FT{
  int x, y;
  FT(int xi, int yi) {
    x=xi;
    y=yi;
  }
}
@Override
public void settings() {
  size(700, 400);
}
void draw() {
  FT[] touches = mousePressed? new FT[]{ new FT(mouseX, mouseY) } : new FT[0];
  
  /*/
void setup2() { }
@Override
public void settings() {
  fullScreen();
}
void draw() {
  mousePressed = touches.length>0;
  //*/
  if (mode != 0) {
    if (touches.length == 2) {
      float ow = dist(touches[0].x, touches[0].y, touches[1].x, touches[0].y);
      double sc = ow/fullow;
      if (fullow != 0) {
        double avgX = (touches[0].x + touches[1].x) / 2f;
        double avgY = (touches[0].y + touches[1].y) / 2f;
        double pS = fullS;
        fullS*= sc;
        double scalechange = 1/fullS - 1/pS;
        fullX-= (avgX * scalechange);
        fullY-= (avgY * scalechange);
      }
      fullow = ow;
    } else {
      fullow = 0;
      if (touches.length == 1) {
        fullX += (pmouseX - mouseX)/fullS;
        fullY += (pmouseY - mouseY)/fullS;
      }
    }
    pushMatrix();
    translate((int)(-fullX * fullS), (int)(-fullY * fullS));
    scale((float)fullS);
    background(16);
    
    if (mode == 1) {
      textAlign(LEFT, TOP);
      textSize(th);
      text(res, 0, h / 10f);
      popMatrix();
    } else if (mode == 2) {
      if (resVal instanceof APL.types.Arr) {
        Arr arr = (Arr)resVal;
        if (arr.ia == 0) {
          res = "no points to draw";
          mode = 0;
          return;
        }
        if (arr.arr[0] instanceof Num) {
          float x = 0;
          int jmp = 10;
          noFill();
          beginShape();
          stroke(0xffd2d2d2);
          strokeWeight(height/200f / (float)fullS);
          for (Value v : arr.arr) {
            float y = -(float) ((Num) v).doubleValue()*jmp;
            vertex(x, y);
            x+=jmp;
          }
          endShape();
          popMatrix();
        }
      } else if (resVal instanceof APL.types.Fun) {
        functionGrapher();
      } else {
        res = "not array";
        mode = 0;
        popMatrix();
      }
    }
    return;
  }
  String prevprog = program;
  if (!pmousePressed && mousePressed) {
    smouseX = mouseX;
    smouseY = mouseY;
    stouch = millis();
  }
  background(16);
  textSize(th);
  textAlign(LEFT,TOP);
  float py = h/10f;
  int cp = 0;
  for (String s : split(program, "\n")) {
    text(s, 0, py);
    if (cp <= ptr && cp + s.length() >= ptr) {
      if ((millis()-lastTouch)%1000<500) text("|", s.length() == 0? -th*.315f : textWidth(s.substring(0, ptr-cp))-th*.3f, py);
    }
    cp+= s.length()+1;
    py+= th;
  }
  textAlign(RIGHT, CENTER);
  
  textSize(resSize);
  text(res, width - 2*w, height-vC*h + h/2f);
  
  textAlign(CENTER, CENTER);
  for (int y = 0; y < vC; y++) {
    for (int x = 0; x < hC; x++) {
      chars[y][x].draw();
      
    }
  }
  float xp = smouseX*1f/w;
  float yp = vC-(height-smouseY)*1f/h-1;
  int x = smouseX/w;
  int y = vC-(height-smouseY)/h-1;
  
  float cx = mouseX*1f/w;
  float cy = vC-(height-mouseY)*1f/h-1;
  
  if (mousePressed && !pmousePressed) {
    if (x >= 0 && x <= hC && y >= 0 && y <= vC)
      started = chars[y][x];
    else started = null;
  }
  
  if (pmousePressed && !mousePressed) {
    if (started != null) started.click(cx-xp, cy-yp);
  }
  if (millis() - stouch > 400 && mousePressed) {
    started.repeat(cx-xp, cy-yp);
    lastTouch = millis();
    stouch += 50;
  }
  
  
  
  
  pmousePressed = mousePressed;
  if (noHist) {
    noHist = false;
  } else if (!prevprog.equals(program)) {
    histptr++;
    if (histptr >= history.length) histptr = 0;
    history[histptr] = program;
    historyPtrs[histptr] = ptr;
    lastTouch = millis();
  }
}
private void ins(String s) {
  for (int i = 0; i < s.length(); i++) {
    program = program.substring(0, ptr)+s.charAt(i)+program.substring(ptr);
    ptr++;
  }
}
private void ins(char c) {
  chars = Dchars;
  program = program.substring(0, ptr)+c+program.substring(ptr);
  ptr++;
}
private int[] pos2d() {
  int y = 0;
  int cp = 0;
  for (String s : split(program, "\n")) {
    if (cp <= ptr && cp + s.length() >= ptr) {
      return new int[]{ptr-cp, y};
    }
    cp+= s.length()+1;
    y+= 1;
  }
  throw new Error("OOB");
}
private void toPos(int[] pos2d) {
  System.out.println(Arrays.toString(pos2d));
  String[] lns = split(program, "\n");
  if (pos2d[1] < 0) pos2d[1] = 0;
  if (pos2d[1] >= lns.length) pos2d[1] = lns.length;
  int y = 0;
  int cp = 0;
  for (String s : lns) {
    if (y == pos2d[1]) {
      ptr = cp + Math.min(pos2d[0], s.length());
      return;
    }
    cp+= s.length()+1;
    y+= 1;
  }
  System.out.println("not found?");
}
