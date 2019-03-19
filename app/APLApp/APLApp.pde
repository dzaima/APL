import java.text.DecimalFormat;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;
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
  if (key == 27) {
    key = 0;
    onBackPressed();
  }
  println(+key, keyCode/*, raw.getKeyChar(), raw.getKeyCode()*/);
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
/*
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
  size(1200, 600, P2D);
}
void copyText(final String s) {
  StringSelection selection = new StringSelection(s);
  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  clipboard.setContents(selection, selection);
}
String gottenClip = null;
void pasteText() {
  try {
    String text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
    gottenClip = text;
  } catch (Throwable e) {
    e.printStackTrace();
    res = e.toString();
  }
}
void draw() {
  FT[] touches = mousePressed? new FT[]{ new FT(mouseX, mouseY) } : new FT[0];
  
  /*/
import android.content.Context;
import android.content.ClipboardManager;
void setup2() { }
void copyText(final String s) {
  getActivity().runOnUiThread(new Runnable() {
    public void run() {
      ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE); 
      clipboard.setText(s);
    }
  });
}
String gottenClip = null;
void pasteText() {
  getActivity().runOnUiThread(new Runnable() {
    public void run() {
      ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE); 
      gottenClip = clipboard.getText().toString();
    }
  });
}
@Override
public void settings() {
  fullScreen(P2D);
}
void draw() {
  mousePressed = touches.length>0;
  //*/
  if (gottenClip != null) {
    ins(gottenClip);
    gottenClip = null;
  }
  if (mode != 0) {
    if (touches.length == 2) {
      float ow = dist(touches[0].x, touches[0].y, touches[1].x, touches[1].y);
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
    
    background(16);
    
    

    //float hl = (float)(-fullY*fullS);
    //line(0, hl, width, hl);
    //float vl = (float)(-fullX*fullS);
    //line(vl, 0, vl, height);
    
    pushMatrix();
    translate((int)(-fullX * fullS), (int)(-fullY * fullS));
    scale((float)fullS);
    
    stroke(0xff666666);
    strokeWeight(1 / (float)fullS);
    if (mode == 2) {
                   
      int freq = 10;
      
      float sx = (float) (fullX/scale);
      float ex = (float) ((fullX + width/fullS)/scale);
      
      float sy = (float) (fullY/scale);
      float ey = (float) ((fullY + height/fullS)/scale);
      
      float rsz = log((ex-sx)/freq)/log(10);
      float sz = pow(10, floor(rsz));
      float m1 = rsz % 1;
      if (m1 < 0) m1+= 1;
      if (m1 > .6) sz*= 5;
      else if (m1 > .3) sz*= 2;
      
      textAlign(LEFT, BOTTOM);
      fill(0xffd2d2d2);
      float ts = width/70f / (float)fullS;
      textSize(ts);
      DecimalFormat df = new DecimalFormat("#.0");
      
      int dgs = ceil(log(1/sz)/log(10));
      df.setMaximumFractionDigits(dgs);
      df.setMinimumFractionDigits(dgs);
      
      
      for (float x = (float)Math.floor(sx/sz) * sz; x < ex; x+= sz) {
        line(x, sy, x, ey);
        float off = ts*1.5;
        float ty = sy>-off? sy+off : ey<0? ey : 0;
        text(df.format(x), x, ty);
      }
      for (float y = (float)Math.floor(sy/sz) * sz; y < ey; y+= sz) {
        line(sx, y, ex, y);
        float off = ts*3;
        float tx = sx>0? sx : ex < off? ex-off : 0;
        text(df.format(-y), tx, y);
      }
    }
    
    
    if (mode == 1) {
      textAlign(LEFT, TOP);
      textSize(th);
      text(res, 0, h / 10f);
      popMatrix();
    } else if (mode == 2) {
      
      
      if (resVal instanceof Value) {
        try {
          float x = 0;
          noFill();
          beginShape();
          stroke(0xffd2d2d2);
          strokeWeight(height/200f / (float)fullS);
          for (double v : ((Value) resVal).asDoubleArr()) {
            float y = -(float) v;
            vertex(x, y);
            x++;
          }
          endShape();
          popMatrix();
        } catch (Throwable e) {
          res = e.getMessage();
          popMatrix();
          mode = 0;
        }
      } else if (resVal instanceof APL.types.Fun) {
        functionGrapher();
      } else {
        res = "can't graph the given";
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
    if (x >= 0 && x < hC && y >= 0 && y < vC)
      started = chars[y][x];
    else started = null;
  }
  
  if (pmousePressed && !mousePressed && started != null) {
    started.click(cx-xp, cy-yp);
  }
  if (millis() - stouch > 400 && mousePressed && started != null) {
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
