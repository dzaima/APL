class Keyboard extends Drawable {
  int xam, yam;
  int kw, kh; // key width/height
  JSONObject data;
  int[] cols;
  int defcol;
  int shiftMode; // 0 = none, 1 = temp, 2 = hold;
  Key[][] keys;
  String layout;
  Keyboard(int x, int y, int w, int h, int xam, int yam, JSONObject data) {
    super(x, y, w, h); // don't draw by default
    this.data = data;
    this.xam = xam;
    this.yam = yam;
    this.kw = w/xam;
    this.kh = h/yam;
    keys = new Key[yam][xam];
    for (int cy = 0; cy < yam; cy++) for (int cx = 0; cx < xam; cx++) keys[cy][cx] = new Key(cx, cy, this);
    JSONArray colsA = data.getJSONArray("colors");
    cols = new int[colsA.size()];
    for(int i = 0; i < cols.length; i++) {
      cols[i] = Integer.parseUnsignedInt(colsA.getString(i), 16);
    }
    defcol = cols[data.getInt("defcol")];
    loadLayout(data.getString("mainName"));
  }
  void loadLayout(String name) {
    layout = name;
    JSONArray arr = data.getJSONArray(name);
    for (int y = 0; y < yam; y++) {
      JSONArray row = arr.getJSONArray(y);
      for (int x = 0; x < xam; x++) {
        keys[y][x].load(row.getJSONObject(x));
      }
    }
    if (layoutUpdate != null) layoutUpdate.call(Main.toAPL(data.getString("fullName")), Main.toAPL(name));
    redraw();
  }
  void redraw() {
    if (visible) {
      for (Key[] row : keys) for (Key k : row) k.redraw();
    }
  }
  
  Key start;
  
  void tick() {
    if (!pmousePressed && mousePressed && smouseIn()) {
      int mx = (mouseX-x) / kw;
      int my = (mouseY-y) / kh;
      if (mx >= 0 && my >= 0 && mx < xam && my < yam) {
        start = keys[my][mx];
        start.redraw();
      }
    }
    if (pmousePressed && !mousePressed && start != null) {
      Action a = findAction();
      Key t = start;
      start = null;
      t.redraw();
      if (a != null) {
        if (actionCalled != null) if (actionCalled.call(new HArr(new Value[]{new Num(t.x), new Num(t.y), new Num(actionId()), Main.toAPL(kb.layout)})).equals(Main.toAPL("stop"))) return;
        a.call();
      }
    }
    if (start != null) {
      Action a = findAction();
      if (a != null) {
        a.k.redraw(a);
        if (millis() - mouseStart > 200) {
          if (a.rep && frameCount%2==1) a.call();
        }
      }
    }
  }
  Action findAction() {
    return start.actions[actionId()];
  }
  int actionId() {
    if (dist(mouseX, mouseY, smouseX, smouseY) > kh/3) { // gesture
      int dx = mouseX - smouseX;
      int dy = mouseY - smouseY;
      if (Math.abs(dx) > Math.abs(dy)) {
        if (dx > 0) return 4;
        else        return 3;
      } else {
        if (dy > 0) return 2;
        else        return 1;
      }
    } else return 0;
  }
}

void keyboard(int x, int y, int w, int h, String file) {
  if (kb != null) kb.delete();
  JSONObject o = loadJSONObject(file);
  JSONArray main = o.getJSONArray(o.getString("mainName"));
  kb = new Keyboard(x, y, w, h, main.getJSONArray(0).size(), main.size(), o);
  kb.align(BOTTOM);
  kb.show();
}

static final String[] dirs = new String[]{"def", "up", "down", "left", "right"};
static final float[][] offsets = {
  {.5, .5 }, // center
  {.5, .2 }, // up
  {.5, .87}, // down
  {.2, .5 }, // left
  {.8, .5 }, // right
};
static final float[][] corners = {
  null,
  {0, 0, 1, 0},
  {0, 1, 1, 1},
  {0, 0, 0, 1},
  {1, 0, 1, 1},
};

class Key extends Drawable {
  Keyboard b;
  
  int col = #222222;
  Action[] actions = new Action[5]; // C U D L R
  
  Key(int x, int y, Keyboard b) {
    super(x, y, b.kw, b.kh);
    this.b = b;
  }
  
  void redraw() {
    redraw(null);
  }
  
  void redraw(Action hl) { // highlight
    rectMode(CORNER);
    fill(b.start == this && (hl == actions[0])? lerpColor(col, #aaaaaa, .1) : col);
    noStroke();
    int px = b.x + x*w;
    int py = b.y + y*h;
    rect(px, py, w, h);
    if (hl != null) {
      for(int i = 1; i < 5; i++) {
        Action a = actions[i];
        if (a == hl) {
          fill(lerpColor(col, #aaaaaa, .1));
          triangle(px+w/2, py+h/2, px + w*corners[i][0], py + h*corners[i][1], px + w*corners[i][2], py + h*corners[i][3]);
          break;
        }
      }
    }
    for(int i = 0; i < 5; i++) {
      Action a = actions[i];
      float[] offs = offsets[i];
      if (a == null) continue;
      String t = a.chr;
      if (b.shiftMode != 0 && t.length() == 1) t = t.toUpperCase();
      fill(255);
      textAlign(CENTER, CENTER);
      float yoff = 0;
      
      if (i == 0) {
        textSize(h*.4f);
      } else {
        textSize(h*.16f);
      }
      
      text(t, px + w*offs[0], py + h*offs[1] + yoff);
    }
  }
  void load(JSONObject o) {
    for (int i = 0; i < 5; i++) {
      JSONObject c = o.getJSONObject(dirs[i]);
      if (c == null) actions[i] = null;
      else actions[i] = new Action(c, b, this);
    }
    col = o.hasKey("col")? b.cols[o.getInt("col")] : b.defcol;
  }
}


class Action {
  final String chr, spec, type, gotof;
  final boolean rep;
  final Keyboard b;
  final Key k;
  Action (JSONObject o, Keyboard b, Key k) {
    chr = o.getString("chr");
    String type = o.getString("type");
    if (type == null) this.type = chr;
    else this.type = type;
    
    spec = o.getString("spec");
    gotof = o.getString("goto");
    rep = o.hasKey("rep")? o.getBoolean("rep") : false;
    this.b = b;
    this.k = k;
  }
  void call() {
    if (textInput == null) return;
    if (spec == null) {
      String toType = type;
      if (b.shiftMode != 0) {
        if (type.length() == 1) toType = toType.toUpperCase();
        if (b.shiftMode == 1) {
          b.shiftMode = 0;
          b.redraw();
        }
      }
      textInput.append(toType);
      return;
    }
    if (gotof != null) {
      b.loadLayout(gotof);
    }
    switch(spec) {
      case "none": return;
      case "del": textInput.ldelete(); return;
      case "rdel": textInput.rdelete(); return;
      case "clear": textInput.clear(); return;
      case "enter": textInput.append("\n"); return;
      case "shift": 
        b.shiftMode++;
        if (b.shiftMode > 2) b.shiftMode = 0;
        b.redraw();
        return;
    }
    textInput.special(spec);
    //println("unknown type "+spec);
  }
}
